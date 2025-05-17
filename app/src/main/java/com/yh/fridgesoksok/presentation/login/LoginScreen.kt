package com.yh.fridgesoksok.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoginSuccess by viewModel.isLoginSuccess.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val systemUiController = rememberSystemUiController()

    // 시스템 UI 설정 및 스낵바 처리
    LaunchedEffect(Unit) {
        systemUiController.setNavigationBarColor(Color.White)

        viewModel.snackBarMessages.collect {
            snackBarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    // 로그인 성공 시 네비게이션
    LaunchedEffect(isLoginSuccess) {
        if (isLoginSuccess) {
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.LoginScreen.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        LoginAppLogoHeader()
        LoginCenterContent()
        LoginActions(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            viewModel = viewModel
        )
        LoginSnackBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            snackBarHostState = snackBarHostState
        )
    }
}

@Composable
fun LoginAppLogoHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo02),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}

@Composable
fun LoginCenterContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        SubcomposeLayout { constraints ->
            val textPlaceable = subcompose("text") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "냉장고 속속에\n오신 것을 환영합니다!",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = CustomGreyColor7
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "3초 가입으로 지금바로 시작해보세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CustomGreyColor5
                    )
                }
            }.map { it.measure(constraints) }

            val textHeight = textPlaceable.sumOf { it.height }
            val textWidth = textPlaceable.maxOf { it.width }

            val imagePlaceable = subcompose("image") {
                Image(
                    painter = painterResource(R.drawable.fridge),
                    contentDescription = null,
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }.map { it.measure(constraints) }.first()

            val centerY = constraints.maxHeight / 2
            val textY = centerY - textHeight / 2
            val imageBottomY = textY - 16.dp.roundToPx()
            val imageY = imageBottomY - imagePlaceable.height

            layout(constraints.maxWidth, constraints.maxHeight) {
                val textX = (constraints.maxWidth - textWidth) / 2
                var currentY = textY
                textPlaceable.forEach {
                    it.place(textX, currentY)
                    currentY += it.height
                }

                val imageX = (constraints.maxWidth - imagePlaceable.width) / 2
                imagePlaceable.place(imageX, imageY)
            }
        }
    }
}

@Composable
fun LoginActions(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BubbleText()
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.Center) {
            SocialLoginButton(R.drawable.kakao) { viewModel.createUserOnChannel(Channel.KAKAO) }
            Spacer(modifier = Modifier.width(20.dp))
            SocialLoginButton(R.drawable.naver) { viewModel.createUserOnChannel(Channel.NAVER) }
        }
        Spacer(modifier = Modifier.height(46.dp))
        OutlinedGuestLoginButton { viewModel.createUserOnChannel(Channel.GUEST) }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun SocialLoginButton(
    iconResId: Int,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.wrapContentSize(),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Unspecified
        ),
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}

@Composable
fun OutlinedGuestLoginButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "임시로 로그인하기",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = CustomGreyColor5
        )
    }
}

@Composable
fun LoginSnackBar(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
) {
    SnackbarHost(
        modifier = modifier.padding(bottom = 48.dp),
        hostState = snackBarHostState
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = Color(0xCC333333),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = it.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }
}