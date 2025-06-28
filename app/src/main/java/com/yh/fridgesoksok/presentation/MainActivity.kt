package com.yh.fridgesoksok.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.presentation.camera.CameraScreen
import com.yh.fridgesoksok.presentation.edit_food.EditFoodScreen
import com.yh.fridgesoksok.presentation.home.HomeScreen
import com.yh.fridgesoksok.presentation.login.LoginScreen
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.onboarding.OnboardingScreen
import com.yh.fridgesoksok.presentation.recipe_detail.RecipeDetailScreen
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
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
                // 라투팅 추출
                val backStackEntry by navController.currentBackStackEntryAsState()
                val route = backStackEntry?.destination?.route
                // 하단 바 색상 셋팅
                val systemUiController = rememberSystemUiController()
                val barColor = when (route) {
                    Screen.OnboardingScreen.route -> MaterialTheme.colorScheme.primary
                    Screen.CameraScreen.route -> Color.Black
                    else -> CustomGreyColor1
                }
                // 하단 바 색상 설정
                LaunchedEffect(route) {
                    systemUiController.setNavigationBarColor(barColor)
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.OnboardingScreen.route,
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
                        val recipe = remember {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<RecipeModel>("recipe")
                        }

                        RecipeDetailScreen(navController, recipe)
                    }

                    composable(Screen.UploadScreen.route) {
                        UploadScreen(navController, activityViewModel)
                    }

                    composable(Screen.EditFoodScreen.route) {
                        EditFoodScreen(navController, activityViewModel)
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