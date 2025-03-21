package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
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
    private val validateUserTokenUseCase: ValidateUserTokenUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        /*
            로컬에 저장된 유저토큰을 로드하여
            API 서버에 해당 토큰이 유효한지 여부를 확인
        */

        val user = loadUser()

        user.refreshToken?.let { validateUserToken(refreshToken = it) }
    }

    // 유저정보 로드
    private fun loadUser(): User = loadUserUseCase()

    // 유저토큰 유효성 확인
    private fun validateUserToken(refreshToken: String) {
        validateUserTokenUseCase(refreshToken = refreshToken).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _isLoading.value = true
                }

                is Resource.Error -> {

                }

                is Resource.Success -> {
                    if (result.data == true) {
                        _userToken.value = refreshToken
                    } else {
                        _userToken.value = ""
                    }

                    _isLoading.value = false
                }
            }
        }.launchIn(viewModelScope)
    }
}