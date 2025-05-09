package com.yh.fridgesoksok.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    val primaryColor = MaterialTheme.colorScheme.primary

    // State
    val isLoading = viewModel.isLoading.collectAsState().value
    val userToken = viewModel.userToken.collectAsState().value
    val systemUiController = rememberSystemUiController()

    // 시스템 바 색상 변경
    LaunchedEffect(Unit) {
        systemUiController.setNavigationBarColor(primaryColor)
    }

    // 토큰 확인 후 네비게이션
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(1300L)
            navigateAfterOnboarding(navController, userToken)
        }
    }

    // Content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = primaryColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo01),
            contentScale = ContentScale.None,
            contentDescription = "logo",
        )
    }
}

private fun navigateAfterOnboarding(
    navController: NavController,
    userToken: String
) {
    val nextRoute = if (userToken.isBlank()) Screen.LoginScreen.route else Screen.HomeScreen.route

    navController.navigate(nextRoute) {
        popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
    }
}


// Lottie state
//      val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
//      val progress by animateLottieCompositionAsState(
//          composition = composition,
//          iterations = LottieConstants.IterateForever
//      )

// Lottie ui
//        LottieAnimation(
//            composition = composition,
//            progress = { progress }
//        )