package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.LoginMethod
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    init {
        login(loginMethod = LoginMethod.KAKAO)
    }

    private fun login(loginMethod: LoginMethod) {
        loginUseCase(loginMethod).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                }

                is Resource.Success -> {
                }
            }
        }.launchIn(viewModelScope)
    }
}