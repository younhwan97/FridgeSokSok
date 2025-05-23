package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ReissueUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserUseCase
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import com.yh.fridgesoksok.presentation.model.LoadingState
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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

        if (!user.isTokenValid()) {
            fail("No refresh token")
            return
        }

        validateToken(user, start)
    }

    // 유저 리프레쉬 토큰 유효성 확인
    private fun validateToken(user: UserModel, startTime: Long) {
        validateUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail("Refresh token validation failed")
                is Resource.Success -> {
                    if (result.data == true) reissueToken(user, startTime)
                    else fail("Invalid RefreshToken")
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬) 토큰 갱신
    private fun reissueToken(user: UserModel, startTime: Long) {
        reissueUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail("Token reissue failed")
                is Resource.Success -> {
                    result.data?.let {
                        val updatedUser = user.withReissuedToken(it.accessToken, it.refreshToken)
                        getDefaultFridge(updatedUser, startTime)
                    } ?: fail("Reissued token is null")
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 기본냉장고 추출
    private fun getDefaultFridge(user: UserModel, startTime: Long) {
        getUserDefaultFridgeUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail("Failed to get default fridge")
                is Resource.Success -> {
                    result.data?.let {
                        val updatedUser = user.withDefaultFridgeId(it.id)
                        updateUserUseCase(updatedUser.toDomain())

                        delayUntilMinimumTime(startTime)
                        _state.value = OnboardingState.Success

                        Log.d("User INFO", updatedUser.toString())
                    } ?: fail("Fridge ID is empty")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fail(reason: String) {
        Log.e("Onboarding", "Fail: $reason")
        _state.value = OnboardingState.Error(reason)
    }

    private suspend fun delayUntilMinimumTime(startTime: Long, minMs: Long = 1000) {
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed < minMs) delay(minMs - elapsed)
    }
}