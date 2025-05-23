package com.yh.fridgesoksok.presentation.onboarding

import com.yh.fridgesoksok.presentation.model.UserModel

sealed class OnboardingState {
    data object Loading : OnboardingState()
    data class Loaded(val user: UserModel) : OnboardingState()
    data object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}