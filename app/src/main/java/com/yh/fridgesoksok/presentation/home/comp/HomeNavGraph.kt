package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
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
import com.yh.fridgesoksok.presentation.recipe.RecipeScreen

@Composable
fun HomeNavGraph(
    mode: HomeUiMode,
    homeNavController: NavHostController,
    navController: NavController,
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
                    sharedViewModel = sharedViewModel
                )
            }
        )
        // 레시피 TAB
        homeTabComposable(
            route = Screen.RecipeTab.route,
            content = {
                RecipeScreen(navController = navController)
            }
        )
        // 계정 TAB
        homeTabComposable(
            route = Screen.AccountTab.route,
            content = {
                AccountScreen(navController = navController)
            }
        )
    }
}

private fun NavGraphBuilder.homeTabComposable(
    route: String,
    content: @Composable () -> Unit
) {
    composable(
        route = route,
        exitTransition = { fadeOut(animationSpec = tween(100)) }
    ) {
        content()
    }
}