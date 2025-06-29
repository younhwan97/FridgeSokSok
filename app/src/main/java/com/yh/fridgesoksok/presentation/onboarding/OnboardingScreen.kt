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
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    appInitializerViewModel: AppInitializerViewModel = hiltViewModel(),
) {
    val onboardingState by onboardingViewModel.state.collectAsState()
    val isInitDone by appInitializerViewModel.isInitializationCompleted.collectAsState()

    // 네비게이션
    LaunchedEffect(onboardingState, isInitDone) {
        if (!isInitDone) return@LaunchedEffect
        when (onboardingState) {
            // 온보딩 성공 시 모든 스택을 제거한 뒤 홈화면으로 이동
            is OnboardingState.Success -> navController.navigate(Screen.HomeScreen.route) { popUpTo(0) { inclusive = true } }
            // 온보딩 실패 시 모든 스택을 제거한 뒤 로그인화면으로 이동
            is OnboardingState.Error -> navController.navigate(Screen.LoginScreen.route) { popUpTo(0) { inclusive = true } }
            else -> Unit
        }
    }

    // 화면
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