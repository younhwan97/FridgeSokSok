package com.yh.fridgesoksok.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // State
    val userToken by viewModel.userToken.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val systemUiController = rememberSystemUiController()

    // 로그인 성공 시 네비게이션
    LaunchedEffect(userToken) {
        if (userToken.isNotBlank()) {
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.LoginScreen.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // 로그인 실패 시 메시지
    LaunchedEffect(Unit) {
        systemUiController.setNavigationBarColor(Color.White)

        viewModel.snackBarMessages
            .distinctUntilChanged()
            .collect { message ->
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo02),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 132.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primaryContainer),
                painter = painterResource(R.drawable.fridge),
                contentDescription = null,
                contentScale = ContentScale.None
            )

            Spacer(modifier = Modifier.height(16.dp))

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BubbleText()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.wrapContentSize(),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { viewModel.createUserToken(Channel.NAVER) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Unspecified
                    ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kakao),
                        contentDescription = "카카오",
                        contentScale = ContentScale.None
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    modifier = Modifier.wrapContentSize(),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { viewModel.createUserToken(Channel.NAVER) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Unspecified
                    ),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.naver),
                        contentDescription = "네이버",
                        contentScale = ContentScale.None
                    )
                }
            }

            Spacer(modifier = Modifier.height(46.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .wrapContentHeight(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "임시로 로그인하기",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomGreyColor5
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            hostState = snackBarHostState,
            snackbar = { data ->
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
                        text = data.visuals.message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        )
    }
}