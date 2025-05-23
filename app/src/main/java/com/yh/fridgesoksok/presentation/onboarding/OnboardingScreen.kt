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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingState by viewModel.state.collectAsState()
    val systemUiController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(onboardingState) {
        // 시스템 UI 설정
        systemUiController.setNavigationBarColor(primaryColor)
        // 네비게이션
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

            else -> { /* Loading 상태일 때는 화면 전환 X */ }
        }
    }

    // Content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = primaryColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo01),
            contentDescription = "AppLogo",
            contentScale = ContentScale.None,
        )
    }
}
