package com.yh.fridgesoksok.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    // User Token
    val userToken = viewModel.userToken.collectAsState().value

    // Lottie state
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Lottie loading image
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )

    LaunchedEffect(Unit) {
        delay(1000L)

        if (userToken.isBlank()){
            navController.navigate(route = Screen.LoginScreen.route)
        } else {
            // navController.navigate(route = Screen.LoginScreen.route)
            navController.navigate(route = Screen.HomeScreen.route)
        }
    }
}