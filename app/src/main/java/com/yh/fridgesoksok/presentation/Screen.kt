package com.yh.fridgesoksok.presentation

sealed class Screen(val route: String) {
    object OnboardingScreen : Screen("Onboarding")
    object HomeScreen : Screen("Home")
    object LoginScreen : Screen("Login")
    object CameraScreen : Screen("Camera")
    object UploadScreen : Screen("Upload")
    object EditFoodScreen : Screen("EditFood")
}