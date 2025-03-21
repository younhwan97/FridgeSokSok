package com.yh.fridgesoksok.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    // State
    val isLoading = viewModel.isLoading.collectAsState().value
    val userToken = viewModel.userToken.collectAsState().value

    // Lottie state
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Loading
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }

    // 토큰 확인 후 화면 전환
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(1300L)

            if (userToken.isBlank()) {
                // 유저 토큰이 없다면 로그인 로그인 화면으로 이동
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
                }
            } else {
                // 유저 토큰이 있다면 홈 화면으로 이동
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
                }
            }
        }
    }
}