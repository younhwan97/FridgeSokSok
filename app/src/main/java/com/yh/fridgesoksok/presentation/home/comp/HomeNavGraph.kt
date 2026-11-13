package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.account.AccountScreen
import com.yh.fridgesoksok.presentation.fridge.FridgeScreen
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.home.HomeViewModel
import com.yh.fridgesoksok.presentation.recipe.RecipeScreen

@Composable
fun HomeNavGraph(
    mode: HomeUiMode,
    homeNavController: NavHostController,
    navController: NavController,
    homeViewModel: HomeViewModel,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = homeNavController,
        startDestination = Screen.FridgeTab.route
    ) {
        // 냉장고 TAB
        homeTabComposable(
            route = Screen.FridgeTab.route,
            content = {
                FridgeScreen(
                    mode = mode,
                    navController = navController,
                    homeNavController = homeNavController,
                    homeViewModel = homeViewModel
                )
            }
        )
        // 레시피 TAB
        homeTabComposable(
            route = Screen.RecipeTab.route,
            content = {
                RecipeScreen(
                    navController = navController,
                    homeNavController = homeNavController,
                    sharedViewModel = sharedViewModel
                )
            }
        )
        // 계정 TAB
        homeTabComposable(
            route = Screen.AccountTab.route,
            content = {
                AccountScreen(
                    navController = navController,
                    homeNavController = homeNavController
                )
            }
        )
    }
}

private const val TAB_FADE_IN_MS = 180
private const val TAB_FADE_OUT_MS = 100

private fun NavGraphBuilder.homeTabComposable(
    route: String,
    content: @Composable () -> Unit
) {
    composable(
        route = route,
        // enter 를 지정하지 않으면 Navigation 기본값(700ms 페이드)이 적용되어 탭 전환이 느리게 느껴짐
        enterTransition = { fadeIn(animationSpec = tween(TAB_FADE_IN_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(TAB_FADE_OUT_MS)) }
    ) {
        content()
    }
}