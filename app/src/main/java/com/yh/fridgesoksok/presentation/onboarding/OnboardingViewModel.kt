package com.yh.fridgesoksok.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.domain.usecase.GetUserTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getUserTokenUseCase: GetUserTokenUseCase
) : ViewModel() {

    private val _userToken = MutableStateFlow<String>("")
    val userToken = _userToken.asStateFlow()

    init {

        _userToken.value = getUserToken() ?: ""
    }

    private fun getUserToken(): String? = getUserTokenUseCase()
}