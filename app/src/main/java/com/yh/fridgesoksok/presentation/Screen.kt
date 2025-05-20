package com.yh.fridgesoksok.presentation

import androidx.annotation.DrawableRes
import com.yh.fridgesoksok.R

sealed class Screen(
    val route: String,
    @DrawableRes val selectedIcon: Int? = null,
    @DrawableRes val unselectedIcon: Int? = null,
    val label: String? = null
) {
    // 시작 관련 화면
    data object OnboardingScreen : Screen("Onboarding")
    data object LoginScreen : Screen("Login")

    // 홈 화면
    data object HomeScreen : Screen("Home")

    // 홈 내부 탭
    data object FridgeTab : Screen("Fridge", R.drawable.home_selected, R.drawable.home, "냉장고")
    data object RecipeTab : Screen("Recipe", R.drawable.recipe_selected, R.drawable.recipe, "레시피")
    data object AccountTab : Screen("Account", R.drawable.account_selected, R.drawable.account, "계정")

    companion object {
        val bottomNavItems = listOf(FridgeTab, RecipeTab, AccountTab)
    }

    // 식품 추가 화면
    data object CameraScreen : Screen("Camera")
    data object UploadScreen : Screen("Upload")
    data object EditFoodScreen : Screen("EditFood")
}