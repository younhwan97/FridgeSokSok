package com.yh.fridgesoksok.presentation.login

sealed class LoginState {
    data object Loading : LoginState()
    data object Success : LoginState()
    data object Error : LoginState()
}