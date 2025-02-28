package com.yh.fridgesoksok.presentation

sealed class Screen(val route: String) {
    object OnboardingScreen : Screen("Onboarding")
    object HomeScreen : Screen("Home")

}