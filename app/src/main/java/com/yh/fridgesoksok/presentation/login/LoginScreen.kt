package com.yh.fridgesoksok.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
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
import com.yh.fridgesoksok.presentation.login.comp.LoginAppLogo
import com.yh.fridgesoksok.presentation.login.comp.LoginButtons
import com.yh.fridgesoksok.presentation.login.comp.LoginCenterContent
import com.yh.fridgesoksok.presentation.login.comp.LoginSnackBar

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    // 스낵바 처리
    LaunchedEffect(Unit) {
        viewModel.snackBarMessages.collect {
            snackBarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
        }
    }

    // 네비게이션
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Content
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

        LoginSnackBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            snackBarHostState = snackBarHostState
        )
    }
}

