package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.CreateUserTokenUseCase
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
) : ViewModel() {

    private val _userToken = MutableStateFlow<String>("")
    val userToken = _userToken.asStateFlow()

    init {
    }

    fun createUserToken(channel: Channel) {
        createUserTokenUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                }

                is Resource.Error -> {
                }

                is Resource.Success -> {
                    setUserToken(token = result.data.toString())
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setUserToken(token: String) {
        setUserTokenUseCase(token = token)
        _userToken.value = token
    }
}