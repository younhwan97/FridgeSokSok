package com.yh.fridgesoksok.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingState by viewModel.state.collectAsState()

    // 네비게이션
    LaunchedEffect(onboardingState) {
        when (onboardingState) {
            is OnboardingState.Success -> {
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            is OnboardingState.Error -> {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            else -> Unit
        }
    }

    // Content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo01),
            contentDescription = "AppLogo",
            contentScale = ContentScale.None,
        )
    }
}
