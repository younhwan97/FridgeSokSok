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

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val systemUiController = rememberSystemUiController()
    val loadingState by viewModel.loadingState.collectAsState()

    // 시스템 UI 설정 및 네비게이션
    LaunchedEffect(loadingState.isLoading) {
        systemUiController.setNavigationBarColor(primaryColor)

        val nextRoute = if (loadingState.isLoadingSuccess) {
            Screen.HomeScreen.route
        } else {
            Screen.LoginScreen.route
        }

        navController.navigate(nextRoute) {
            popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
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
            contentDescription = "logo",
            contentScale = ContentScale.None,
        )
    }
}