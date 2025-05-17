package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.CreateUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserTokenUseCase: CreateUserTokenUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase
) : ViewModel() {

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess

    private val _user = MutableStateFlow(UserModel(-1, null, null, null, null))
    val user = _user.value

    private val _snackBarMessages = MutableSharedFlow<String>()
    val snackBarMessages = _snackBarMessages.asSharedFlow()

    private fun fail() {
        viewModelScope.launch {
            _isLoginSuccess.value = false
            _snackBarMessages.emit("로그인에 실패하였습니다.")
        }
    }

    fun createUserToken(channel: Channel) {
        // 유저토큰 생성
        createUserTokenUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail()

                is Resource.Success -> {
                    val data = result.data
                    if (data != null && data.id != -1L && data.accessToken != null && data.refreshToken != null) {
                        createUser(data)
                    } else {
                        fail()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createUser(user: User) {
        // 유저 생성
        createUserUseCase(user = user).onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail()

                is Resource.Success -> {
                    val data = result.data
                    if (data != null && data.id != -1L && data.accessToken != null && data.refreshToken != null) {
                        _user.value = data.toPresentation()
                        saveUser()
                        // getUserDefaultFridge()
                        _isLoginSuccess.value = true
                    } else {
                        fail()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun saveUser() {
        saveUserUseCase(_user.value.toDomain())
    }

    private fun getUserDefaultFridge() {
        getUserDefaultFridgeUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> fail()

                is Resource.Success -> {
                    if (result.data != null) {
                        _isLoginSuccess.value = true
                    } else {
                        fail()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}