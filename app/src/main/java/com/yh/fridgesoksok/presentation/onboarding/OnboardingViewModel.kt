package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ReissueUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserUseCase
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val validateUserTokenUseCase: ValidateUserTokenUseCase,
    private val reissueUserTokenUseCase: ReissueUserTokenUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val state = _state.asStateFlow()

    init {
        Logger.i("Onboarding", "온보딩 시작")
        /* 온보딩 프로세스
        1. 로컬 유저정보 로드
        2. 토큰 유효성 확인
        3. 토큰 갱신
        4. 유저 기본냉장고 ID 추출
        5. 로컬 유저정보 업데이트 */

        startOnboarding()
    }

    private fun startOnboarding() {
        val start = System.currentTimeMillis()
        val user = UserModel.fromLocal(loadUserUseCase())

        if (!user.isValid()) return fail("토큰 없음")
        validateToken(user, start)
    }

    // 유저 리프레쉬 토큰 유효성 확인
    private fun validateToken(user: UserModel, startTime: Long) {
        validateUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isTokenValid = result.data ?: false
                    if (!isTokenValid) return@onEach fail("리프레쉬 토큰 무효")

                    Logger.d("Onboarding", "리프레쉬 토큰 유효")
                    reissueToken(user, startTime)
                }

                is Resource.Error -> fail("리프레쉬 토큰 검증오류 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬) 토큰 갱신
    private fun reissueToken(user: UserModel, startTime: Long) {
        reissueUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val tokenModel = result.data?.toPresentation()
                    if (tokenModel == null || !tokenModel.isValid()) return@onEach fail("토큰 유효하지 않음")

                    Logger.d("Onboarding", "토큰 갱신 성공")
                    val updatedUser = user.withReissuedToken(tokenModel.accessToken, tokenModel.refreshToken)
                    getDefaultFridge(updatedUser, startTime)
                }

                is Resource.Error -> fail("토큰 갱신 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 기본냉장고 추출
    private fun getDefaultFridge(user: UserModel, startTime: Long) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val fridgeModel = result.data?.toPresentation()
                    if (fridgeModel == null || !fridgeModel.isValid()) return@onEach fail("기본 냉장고ID 추출 실패")

                    Logger.d("Onboarding", "기본 냉장고ID 추출 성공")
                    val updatedUser = user.withDefaultFridgeId(fridgeModel.id)
                    updateUser(updatedUser, startTime)
                }

                is Resource.Error -> fail("기본 냉장고ID 추출 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 저장
    private fun updateUser(user: UserModel, startTime: Long) {
        updateUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isUserUpdated = result.data ?: false
                    if (!isUserUpdated) return@onEach fail("유저 저장 실패")

                    viewModelScope.launch {
                        delayUntilMinimumTime(startTime)
                        _state.value = OnboardingState.Success
                    }

                    Logger.d("Onboarding", "유저 저장 성공 $user")
                    Logger.i("Onboarding", "온보딩 완료")
                }

                is Resource.Error -> fail("유저 저장 실패 ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun fail(reason: String) {
        Logger.e("Onboarding", "$reason → 온보딩 실패")
        _state.value = OnboardingState.Error
    }

    private suspend fun delayUntilMinimumTime(startTime: Long, minMs: Long = 1000) {
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < minMs) delay(minMs - elapsed)
    }
}