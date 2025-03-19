package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow<String>("")
    val userToken = _userToken.asStateFlow()

    init {

        //_userToken.value = getUserToken() ?: ""
        val user = getUserToken()

        Log.d("test5", user.toString())
    }

    private fun getUserToken(): User = loadUserUseCase()
}