package com.yh.fridgesoksok.presentation.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.account.comp.SettingActionRow
import com.yh.fridgesoksok.presentation.account.comp.SettingSection
import com.yh.fridgesoksok.presentation.account.comp.SettingSwitchRow
import com.yh.fridgesoksok.presentation.common.ConfirmDialog
import com.yh.fridgesoksok.presentation.theme.CustomErrorColor
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5

@Composable
fun AccountScreen(
    navController: NavController,
    homeNavController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val userSetting by viewModel.userSetting.collectAsState()
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // 냉장고 섹션
        SettingSection("냉장고") {
            SettingSwitchRow(
                label = "소비기한 알림",
                checked = userSetting?.receiveNotification ?: false
            ) {
                viewModel.updateReceiveNotification(userSetting?.receiveNotification)
            }

            SettingSwitchRow(
                label = "소비기한 지난식품 자동삭제",
                checked = userSetting?.autoDeleteExpiredFoods ?: false
            ) {
                viewModel.updateAutoDeleteExpired(userSetting?.autoDeleteExpiredFoods)
            }
        }

        // 레시피 섹션
        SettingSection("레시피") {
            SettingSwitchRow(
                label = "레시피 생성시 재료모두 사용",
                checked = userSetting?.useAllIngredients ?: false
            ) {
                viewModel.updateUseAllIngredients(!(userSetting?.useAllIngredients ?: false))
            }
        }

        // 서비스 섹션
        SettingSection("서비스") {
            SettingActionRow(
                label = "구독 관리(업데이트 예정)",
                enabled = false,
                textColor = CustomGreyColor3,
                showArrow = true
            ) {

            }
        }

        // 기타 섹션
        SettingSection("기타") {
            SettingActionRow("회원탈퇴", textColor = CustomGreyColor5) {
                showWithdrawDialog = true
            }

            SettingActionRow("로그아웃") {
                showLogoutDialog = true
            }
        }
    }

    // 로그아웃 확인 다이얼로그
    if (showLogoutDialog) {
        ConfirmDialog(
            title = "알림",
            message = "정말 로그아웃 하시겠습니까?",
            onConfirm = {
                viewModel.clearUser()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // 회원탈퇴 확인 다이얼로그
    if (showWithdrawDialog) {
        ConfirmDialog(
            title = "알림",
            message = "정말로 탈퇴 하시겠습니까?\n\n모든 데이터가 삭제됩니다.",
            confirmText = "탈퇴하기",
            confirmTextColor = CustomErrorColor,
            confirmContainerColor = MaterialTheme.colorScheme.errorContainer,
            onConfirm = {
                viewModel.clearUser()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
                showWithdrawDialog = false
            },
            onDismiss = { showWithdrawDialog = false }
        )
    }
}