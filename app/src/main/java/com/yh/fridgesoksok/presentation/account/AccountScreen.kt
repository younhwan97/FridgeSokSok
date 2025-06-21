package com.yh.fridgesoksok.presentation.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.account.comp.SettingActionRow
import com.yh.fridgesoksok.presentation.account.comp.SettingSectionTitle
import com.yh.fridgesoksok.presentation.account.comp.SettingSwitchRow
import com.yh.fridgesoksok.presentation.common.Divider
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5

@Composable
fun AccountScreen(
    navController: NavController,
    homeNavController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val isExpirationAlarmEnabled by viewModel.isExpirationAlarmEnabled.collectAsState()
    val isAutoDeleteExpiredFoodEnabled by viewModel.isAutoDeleteExpiredFoodEnabled.collectAsState()
    val isUseAllIngredientsEnabled by viewModel.isUseAllIngredientsEnabled.collectAsState()

    val scrollState = rememberScrollState()

    // 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        SettingSectionTitle("냉장고")
        Divider(thickness = 2.dp)
        SettingSwitchRow("소비기한 알림", isExpirationAlarmEnabled) { viewModel.updateExpirationAlarmEnabled(!isExpirationAlarmEnabled) }
        SettingSwitchRow(
            "소비기한 지난식품 자동삭제",
            isAutoDeleteExpiredFoodEnabled
        ) { viewModel.updateAutoDeleteExpiredFoodEnabled(!isAutoDeleteExpiredFoodEnabled) }

        Spacer(modifier = Modifier.height(10.dp))

        SettingSectionTitle("레시피")
        Divider(thickness = 2.dp)
        SettingSwitchRow("레시피 생성시 재료모두 사용", isUseAllIngredientsEnabled) { viewModel.updateUseAllIngredientsEnabled(!isUseAllIngredientsEnabled) }

        Spacer(modifier = Modifier.height(10.dp))

        SettingSectionTitle("서비스")
        Divider(thickness = 2.dp)
        SettingActionRow("구독 관리(업데이트 예정)", enabled = false, textColor = CustomGreyColor3, showArrow = true) {}

        Spacer(modifier = Modifier.height(10.dp))

        SettingSectionTitle("기타")
        Divider(thickness = 2.dp)
        SettingActionRow("회원탈퇴", textColor = CustomGreyColor5) {
            viewModel.clearUser()
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }

        SettingActionRow("로그아웃") {
            viewModel.clearUser()
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}