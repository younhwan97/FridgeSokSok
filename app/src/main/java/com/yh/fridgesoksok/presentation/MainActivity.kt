package com.yh.fridgesoksok.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.camera.CameraScreen
import com.yh.fridgesoksok.presentation.edit_food.EditFoodScreen
import com.yh.fridgesoksok.presentation.home.HomeScreen
import com.yh.fridgesoksok.presentation.login.LoginScreen
import com.yh.fridgesoksok.presentation.onboarding.OnboardingScreen
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

                NavHost(
                    navController = navController,
                    startDestination = Screen.OnboardingScreen.route,
                ) {
                    composable(
                        Screen.OnboardingScreen.route
                    ) {
                        OnboardingScreen(navController)
                    }

                    composable(
                        Screen.LoginScreen.route
                    ) {
                        LoginScreen(navController)
                    }

                    composable(
                        Screen.HomeScreen.route
                    ) {
                        HomeScreen(navController, activityViewModel)
                    }

                    composable(
                        Screen.UploadScreen.route
                    ) {
                        UploadScreen(navController, activityViewModel)
                    }

                    composable(
                        Screen.EditFoodScreen.route
                    ) {
                        EditFoodScreen(navController, activityViewModel)
                    }

                    composable(
                        Screen.CameraScreen.route,
                        enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                        exitTransition = { slideOutVertically(targetOffsetY = { it }) + fadeOut() },
                    ) {
                        CameraScreen(navController, activityViewModel)
                    }
                }
            }
        }
    }
}