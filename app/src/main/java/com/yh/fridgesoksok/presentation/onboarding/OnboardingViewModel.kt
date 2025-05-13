package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ReissueUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _user = MutableStateFlow(User(-1, "", "", "", ""))
    val user = _user.asStateFlow()

    init {
        /*
            로컬에 저장된 유저토큰을 로드하여
            서버에 해당 토큰이 유효한지 여부를 확인
        */

        val user = loadUser()

        user.refreshToken?.takeIf { it.isNotBlank() }?.let {
            validateUserToken(refreshToken = it)
        } ?: run {
            _isLoading.value = false
            _userToken.value = ""
        }
    }

    // 유저정보 로드
    private fun loadUser(): User = loadUserUseCase()

    // 유저 (리프레쉬)토큰 유효성 확인
    private fun validateUserToken(refreshToken: String) {
        validateUserTokenUseCase(refreshToken = refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {}

                is Resource.Error -> {
                    _isLoading.value = false
                    _userToken.value = ""
                }

                is Resource.Success -> {
                    if (result.data == true) {
                        // 리프레쉬 토큰이 유효할 때
                        reissueUserToken(refreshToken = refreshToken)
                    } else {
                        // 리프레쉬 토큰이 유효하지 않을 때
                        _userToken.value = ""
                        _isLoading.value = false
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬)토큰 갱신
    private fun reissueUserToken(refreshToken: String) {
        reissueUserTokenUseCase(refreshToken = refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {}

                is Resource.Error -> {
                    _isLoading.value = false
                    _userToken.value = ""
                }

                is Resource.Success -> {
                    result.data?.let {
                        _user.value = User(
                            id = -1,
                            accessToken = result.data.accessToken,
                            refreshToken = result.data.refreshToken,
                            username = null,
                            accountType = null
                        )

                        getUserDefaultFridge()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 기본냉장고 추출
    private fun getUserDefaultFridge() {
        getUserDefaultFridgeUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}

                is Resource.Error -> {
                    _isLoading.value = false
                    _userToken.value = ""
                }

                is Resource.Success -> {
                    result.data?.let {
                        saveUser(_user.value)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 유저 정보 저장
    private fun saveUser(user: User) {
        saveUserUseCase(user = user)
        // 화면 전환
        _userToken.value = user.accessToken!!
        _isLoading.value = false
    }
}