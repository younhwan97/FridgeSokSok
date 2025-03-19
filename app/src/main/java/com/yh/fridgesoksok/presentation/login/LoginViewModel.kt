package com.yh.fridgesoksok.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.CreateUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserUseCase
import com.yh.fridgesoksok.domain.usecase.SetUserTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserTokenUseCase: CreateUserTokenUseCase,
    private val setUserTokenUseCase: SetUserTokenUseCase,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken = _userToken.asStateFlow()

    fun createUserToken(channel: Channel) {
        createUserTokenUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                }

                is Resource.Success -> {
                    setUserToken(token = result.data?.id.toString())

                    createUser(token = result.data?.accessToken!!, username = "")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun createUser(token: String, username: String) {
        Log.d("test4", "시작")
        createUserUseCase(token = token, username = username).launchIn(viewModelScope)
        Log.d("test4", "종료")
    }

    private fun setUserToken(token: String) {
        setUserTokenUseCase(token = token)
        _userToken.value = token
    }
}