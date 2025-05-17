package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.CreateUserOnChannelUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserOnServerUseCase
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
    private val createUserOnChannelUseCase: CreateUserOnChannelUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserOnServerUseCase: CreateUserOnServerUseCase,
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

    fun createUserOnChannel(channel: Channel) {
        // (네이버, 카카오 등의) 채널로 부터 유저 생성
        createUserOnChannelUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail()

                is Resource.Success -> {
                    val data = result.data
                    if (data != null && data.id != -1L && data.accessToken != null && data.refreshToken != null) {
                        createUserOnServer(data)
                    } else {
                        fail()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createUserOnServer(user: User) {
        // (채널에서 생성한 토큰을 활용해 서버에서) 유저 생성
        createUserOnServerUseCase(user = user).onEach { result ->
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