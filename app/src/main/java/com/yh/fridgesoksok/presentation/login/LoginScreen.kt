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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.login.comp.LoginAppLogo
import com.yh.fridgesoksok.presentation.login.comp.LoginButtons
import com.yh.fridgesoksok.presentation.login.comp.LoginCenterContent
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val snackBarHostState = remember { SnackbarHostState() }
    val loginState by viewModel.state.collectAsState()

    // 하단 바 색상 설정
    SideEffect {
        systemUiController.setNavigationBarColor(CustomGreyColor1)
    }

    // 에러 메시지를 스낵바로 표시
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    // 로그인 성공 시 홈 화면으로 이동
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        // 상단 앱 로고
        LoginAppLogo(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // 중앙 컨텐츠 (냉장고 이미지 및 텍스트)
        LoginCenterContent()

        // 하단 로그인 버튼들 (카카오, 네이버, 게스트)
        LoginButtons(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onLogin = viewModel::createUserOnChannel
        )

        // 에러 메시지 스낵바
        Snackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            snackBarHostState = snackBarHostState
        )
    }
}