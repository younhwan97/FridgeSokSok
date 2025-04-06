package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.CreateUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserTokenUseCase: CreateUserTokenUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    fun createUserToken(channel: Channel) {
        // 유저토큰 생성
        createUserTokenUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                }

                is Resource.Success -> {
                    // 유저 생성
                    result.data?.let { createUser(it) } ?: run {
                        // 유저토큰을 생성하는데 실패했을 때
                        
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createUser(user: User) {
        createUserUseCase(user = user).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                }

                is Resource.Success -> {
                    // 유저 정보 저장
                    result.data?.accessToken?.let { saveUser(result.data) } ?: run {
                        // 유저 생성에 실패했을 때
                    }
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