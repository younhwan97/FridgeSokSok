package com.yh.fridgesoksok.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.camera.CameraScreen
import com.yh.fridgesoksok.presentation.edit_food.EditFoodScreen
import com.yh.fridgesoksok.presentation.home.HomeScreen
import com.yh.fridgesoksok.presentation.login.LoginScreen
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.onboarding.OnboardingScreen
import com.yh.fridgesoksok.presentation.recipe_detail.RecipeDetailScreen
import com.yh.fridgesoksok.presentation.theme.FridgeSokSokTheme
import com.yh.fridgesoksok.presentation.upload.UploadScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeSokSokTheme {
                val navController = rememberNavController()
                // 전역 뷰모델 (Activity-Scoped ViewModel)
                val activityViewModel: SharedViewModel = hiltViewModel()

                // 세션 만료 시 백스택을 비우고 로그인 화면으로 이동
                LaunchedEffect(Unit) {
                    activityViewModel.sessionExpired.collect {
                        if (navController.currentDestination?.route == Screen.LoginScreen.route) return@collect
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.OnboardingScreen.route,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    composable(Screen.OnboardingScreen.route) {
                        OnboardingScreen(navController)
                    }

                    composable(Screen.LoginScreen.route) {
                        LoginScreen(navController)
                    }

                    composable(Screen.HomeScreen.route) {
                        HomeScreen(navController, activityViewModel)
                    }

                    composable(Screen.RecipeDetailScreen.route) {
                        RecipeDetailScreen(navController, activityViewModel)
                    }

                    composable(Screen.UploadScreen.route) {
                        UploadScreen(navController, activityViewModel)
                    }

                    composable(Screen.EditFoodScreen.route) {
                        EditFoodScreen(navController)
                    }

                    composable(
                        Screen.CameraScreen.route,
                        enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        CameraScreen(navController, activityViewModel)
                    }
                }
            }
        }
    }
}