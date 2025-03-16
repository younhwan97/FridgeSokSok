package com.yh.fridgesoksok.presentation.login

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.presentation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    if (viewModel.userToken.collectAsState().value.isNotEmpty()) {
        navController.navigate(route = Screen.HomeScreen.route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(192.dp))

            Text(
                text = "모바일 냉장고를\n만들어 볼까요?",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(96.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xFFFEE500))
                    .clickable { viewModel.createUserToken(channel = Channel.KAKAO) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 12.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.kakak_logo),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .align(Alignment.CenterStart)

                    )
                }

                Text(
                    text = "카카오 로그인",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xFF03C75A))
                    .clickable { Unit }
            ) {
                Image(
                    painter = painterResource(R.drawable.naver_icon),
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(48.dp)
                )

                Text(
                    text = "네이버 로그인",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}