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

    private val _loadingState = MutableStateFlow(LoadingState())
    val loadingState = _loadingState.asStateFlow()

    private val _user = MutableStateFlow(UserModel(-1, null, null, null, null))
    val user = _user.value

    init {
        /* 온보딩 프로세스
           1. 로컬 유저정보 로드
           2. 토큰이 유효성 확인
           3. 유저토큰 갱신
           4. 로컬 유저정보 업데이트
           5. 유저 기본냉장고 ID 추출 */

        _user.value = loadUserUseCase().toPresentation()

        _user.value.refreshToken
            ?.takeIf { it.isNotBlank() }
            ?.let {
                validateUserToken(it)
            } ?: fail("")
    }

    private fun fail(reason: String? = null) {
        reason?.let { Log.e("Onboarding", "Fail reason: $it") }
        _loadingState.update { it.copy(isLoading = false, isLoadingSuccess = false) }
    }

    // 유저 리프레쉬 토큰 유효성 확인
    private fun validateUserToken(refreshToken: String) {
        validateUserTokenUseCase(refreshToken = refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail()

                is Resource.Success -> {
                    if (result.data == true) reissueUserToken(refreshToken = refreshToken)
                    else fail("Invalid RefreshToken")
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬) 토큰 갱신
    private fun reissueUserToken(refreshToken: String) {
        reissueUserTokenUseCase(refreshToken = refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail()

                is Resource.Success -> {
                    result.data?.let { newToken ->
                        _user.update {
                            it.copy(
                                accessToken = newToken.accessToken,
                                refreshToken = newToken.refreshToken
                            )
                        }
                        updateUser()
                        getUserDefaultFridge()
                    } ?: fail("Reissued token is null")
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 정보 저장
    private fun updateUser() {
        updateUserUseCase(_user.value.toDomain())
    }

    // 유저 기본냉장고 추출
    private fun getUserDefaultFridge() {
        getUserDefaultFridgeUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail()

                is Resource.Success -> {
                    if (!result.data.isNullOrBlank()) {
                        _loadingState.update {
                            it.copy(
                                isLoading = false,
                                isLoadingSuccess = true
                            )
                        }
                    } else {
                        fail("Default fridge is empty")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}