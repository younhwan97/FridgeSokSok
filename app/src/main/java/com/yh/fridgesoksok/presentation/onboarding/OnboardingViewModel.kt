package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val validateUserTokenUseCase: ValidateUserTokenUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow<String>("")
    val userToken = _userToken.asStateFlow()

    init {

        //_userToken.value = getUserToken() ?: ""
        val user = getUserToken()

        Log.d("test4", user.toString())

        validateUserTokenUseCase(refreshToken = user.refreshToken ?: "").launchIn(viewModelScope)

        Log.d("test5", user.toString())
    }

    private fun getUserToken(): User = loadUserUseCase()
}