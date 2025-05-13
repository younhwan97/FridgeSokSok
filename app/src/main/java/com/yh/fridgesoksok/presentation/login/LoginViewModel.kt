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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserTokenUseCase: CreateUserTokenUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    private val _snackBarMessages = MutableSharedFlow<String>()
    val snackBarMessages = _snackBarMessages.asSharedFlow()

    fun createUserToken(channel: Channel) {
        // 유저토큰 생성
        createUserTokenUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                    _snackBarMessages.emit("로그인에 실패하였습니다.")
                }

                is Resource.Success -> {
                    result.data
                        ?.takeIf { it.id != -1L }
                        ?.let(::createUser)
                        ?: _snackBarMessages.emit("로그인에 실패하였습니다.")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createUser(user: User) {
        // 유저 생성
        createUserUseCase(user = user).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                    _snackBarMessages.emit("로그인에 실패하였습니다.")
                }

                is Resource.Success -> {
                    result.data
                        ?.takeIf { it.id != -1L && it.accessToken != null }
                        ?.let { userWithToken ->
                            getUserDefaultFridge(userWithToken)
                        } ?: _snackBarMessages.emit("로그인에 실패하였습니다.")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserDefaultFridge(user: User) {
        getUserDefaultFridgeUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}

                is Resource.Error -> {
                    _snackBarMessages.emit("로그인에 실패하였습니다.")
                }

                is Resource.Success -> {
                    saveUser(user) // 👈 성공 시 저장
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun saveUser(user: User) {
        // 유저 정보 저장
        saveUserUseCase(user = user)
        // 화면 전환
        _userToken.value = user.accessToken!!
    }
}