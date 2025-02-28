package com.yh.fridgesoksok.presentation

sealed class Screen(val route: String) {
    object OnboardingScreen : Screen("Onboarding")
    object FoodListScreen : Screen("FoodList")

}