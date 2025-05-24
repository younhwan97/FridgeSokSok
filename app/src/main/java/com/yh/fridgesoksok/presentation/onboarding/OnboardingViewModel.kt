package com.yh.fridgesoksok.presentation.onboarding

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val validateUserTokenUseCase: ValidateUserTokenUseCase,
    private val reissueUserTokenUseCase: ReissueUserTokenUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val state = _state.asStateFlow()

    init {
        Logger.i("Onboarding", "온보딩 시작")
        /* 온보딩 프로세스
        1. 로컬 유저정보 로드
        2. 토큰이 유효성 확인
        3. 유저토큰 갱신
        4. 유저 기본냉장고 ID 추출
        5. 로컬 유저정보 업데이트 */

        startOnboarding()
    }

    private fun startOnboarding() {
        val start = System.currentTimeMillis()
        val user = UserModel.fromLocal(loadUserUseCase())

        if (user.refreshToken.isBlank()) {
            return fail("토큰 없음")
        }

        validateToken(user, start)
    }

    // 유저 리프레쉬 토큰 유효성 확인
    private fun validateToken(user: UserModel, startTime: Long) {
        validateUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data == true) {
                        Logger.d("Onboarding", "리프레쉬 토큰 유효")
                        reissueToken(user, startTime)
                    } else {
                        fail("리프레쉬 토큰 무효")
                    }
                }

                is Resource.Error -> fail("토큰 검증 오류")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬) 토큰 갱신
    private fun reissueToken(user: UserModel, startTime: Long) {
        reissueUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        Logger.d("Onboarding", "토큰 갱신 성공")
                        val updatedUser = user.withReissuedToken(it.accessToken, it.refreshToken)
                        getDefaultFridge(updatedUser, startTime)
                    } ?: run {
                        fail("토큰 갱신 실패 (null)")
                    }
                }

                is Resource.Error -> fail("토큰 갱신 실패")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 기본냉장고 추출
    private fun getDefaultFridge(user: UserModel, startTime: Long) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        val updatedUser = user.withDefaultFridgeId(it.id)
                        updateUserUseCase(updatedUser.toDomain())

                        delayUntilMinimumTime(startTime)
                        _state.value = OnboardingState.Success

                        Logger.d("Onboarding", "유저정보 셋팅 성공 $updatedUser")
                        Logger.i("Onboarding", "온보딩 완료")
                    } ?: run {
                        fail("기본 냉장고ID 없음 (data=null)")
                    }
                }

                is Resource.Error -> fail("기본 냉장고ID 조회 실패")
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