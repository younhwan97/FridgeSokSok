package com.yh.fridgesoksok.presentation.onboarding

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog

@Composable
fun OnboardingScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    appInitializerViewModel: AppInitializerViewModel = hiltViewModel(),
) {
    val onboardingState by onboardingViewModel.state.collectAsState()
    val isInitDone by appInitializerViewModel.isInitializationCompleted.collectAsState()
    val isServerConnected by onboardingViewModel.isServerConnected.collectAsState()

    // 온보딩 완료 후 네비게이션 처리
    LaunchedEffect(isInitDone, isServerConnected, onboardingState) {
        // 앱 초기화 대기
        if (!isInitDone) return@LaunchedEffect

        // 서버 연결 실패 시 대기
        if (!isServerConnected) return@LaunchedEffect

        // 온보딩 결과에 따른 네비게이션
        val targetRoute = when (onboardingState) {
            is OnboardingState.Success -> Screen.HomeScreen.route
            is OnboardingState.Error -> Screen.LoginScreen.route
            is OnboardingState.Loading -> return@LaunchedEffect
        }

        navController.navigate(targetRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 앱 로고
            Image(
                painter = painterResource(R.drawable.logo01),
                contentDescription = "App Logo",
                contentScale = ContentScale.None,
            )
        }

        // 로딩 상태 표시
        if (isServerConnected && (!isInitDone || onboardingState is OnboardingState.Loading)) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 128.dp)
            ) {
                Text(
                    text = if (!isInitDone) "앱 초기화 중..." else "로그인 확인 중...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // 서버 연결 실패 다이얼로그
    if (!isServerConnected) {
        val context = LocalContext.current
        ConfirmDialog(
            message = "서버 연결에 실패하였습니다 :(\n네트워크 연결을 확인해주세요.",
            confirmText = "재시도",
            confirmTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            confirmTextVisibility = true,
            confirmContainerColor = MaterialTheme.colorScheme.primaryContainer,
            onConfirm = {
                onboardingViewModel.retryOnboarding()
            },
            dismissTextVisibility = true,
            dismissText = "종료",
            onDismiss = {
                (context as? Activity)?.finishAffinity()
            }
        )
    }
}