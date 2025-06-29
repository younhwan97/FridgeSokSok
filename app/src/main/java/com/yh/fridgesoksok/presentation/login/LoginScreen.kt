package com.yh.fridgesoksok.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.login.comp.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.state.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    // 로그인 실패 시 스낵바 메시징
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // 네비게이션
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            // 로그인 성공 시 모든 스택을 제거한 뒤 홈화면으로 이동
            navController.navigate(Screen.HomeScreen.route) { popUpTo(0) { inclusive = true } }
        }
    }

    // 화면
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        LoginAppLogo(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        LoginCenterContent()

        LoginButtons(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onLogin = viewModel::createUserOnChannel
        )

        Snackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            snackBarHostState = snackBarHostState
        )
    }
}