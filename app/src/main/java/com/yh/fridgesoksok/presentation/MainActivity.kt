package com.yh.fridgesoksok.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.camera.CameraScreen
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

                NavHost(
                    navController = navController,
                    startDestination = Screen.OnboardingScreen.route,
                ) {
                    composable(route = Screen.OnboardingScreen.route) {
                        OnboardingScreen(
                            navController = navController
                        )
                    }

                    composable(route = Screen.LoginScreen.route) {
                        LoginScreen(
                            navController = navController
                        )
                    }

                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen(
                            navController = navController
                        )
                    }

                    composable(
                        route = Screen.CameraScreen.route,
                        enterTransition = { slideInVertically(initialOffsetY = { it }) },
                        exitTransition = { slideOutVertically(targetOffsetY = { it }) }) {
                        CameraScreen(
                            navController = navController
                        )
                    }

                    composable(route = Screen.UploadScreen.route){
                        UploadScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}