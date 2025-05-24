package com.yh.fridgesoksok.presentation.onboarding

sealed class OnboardingState {
    data object Loading : OnboardingState()
    data object Success : OnboardingState()
    data object Error : OnboardingState()
}