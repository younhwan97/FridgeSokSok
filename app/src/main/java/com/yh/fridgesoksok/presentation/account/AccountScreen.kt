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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.common.Constants
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.extension.*
import com.yh.fridgesoksok.presentation.account.comp.*
import com.yh.fridgesoksok.presentation.common.comp.*
import com.yh.fridgesoksok.presentation.common.util.*
import com.yh.fridgesoksok.presentation.theme.*

@Composable
fun AccountScreen(
    navController: NavController,
    homeNavController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val userSetting by viewModel.userSetting.collectAsState()
    val hasSystemNotificationPermission by rememberNotificationPermissionState()

    // 다이얼로그 상태
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown(delayMillis = 800)

    // 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        // 냉장고 섹션
        SettingSection(Constants.FRIDGE) {
            SettingSwitchRow(
                label = "소비기한 알림",
                checked = hasSystemNotificationPermission && (userSetting?.receiveNotification == true)
            ) {
                if (hasSystemNotificationPermission) {
                    triggerCooldown()
                    viewModel.updateReceiveNotification(userSetting?.receiveNotification)
                } else {
                    // 시스템 권한이 없을 때
                    showPermissionDialog = true
                }
            }

            SettingSwitchRow(
                label = "소비기한 지난식품 자동삭제",
                checked = userSetting?.autoDeleteExpiredFoods
            ) {
                triggerCooldown()
                viewModel.updateAutoDeleteExpired(userSetting?.autoDeleteExpiredFoods)
            }
        }

        // 레시피 섹션
        SettingSection(Constants.RECIPE) {
            SettingSwitchRow(
                label = "레시피 생성시 모든재료 사용",
                checked = userSetting?.useAllIngredients
            ) {
                triggerCooldown()
                viewModel.updateUseAllIngredients(userSetting?.useAllIngredients)
            }
        }

        // 서비스 섹션
        SettingSection(Constants.SERVICE) {
            SettingActionRow(
                label = "구독 관리(업데이트 예정)",
                enabled = false,
                showArrow = true,
                textColor = CustomGreyColor3
            ) {

            }
        }

        // 기타 섹션
        SettingSection(Constants.ETC) {
            SettingActionRow("회원탈퇴", textColor = CustomGreyColor5) {
                showWithdrawDialog = true
            }

            SettingActionRow("로그아웃") {
                showLogoutDialog = true
            }

//            SettingActionRow("메시지(TEST)", textColor = CustomGreyColor5) {
//                viewModel.sendFcmMessageOnlyTest()
//            }
        }
    }

    // 알림권한 확인 다이얼로그
    if (showPermissionDialog) {
        ConfirmDialog(
            message = "앱 알림을 받으시려면 알림 권한을 켜야 해요.\n\n설정 화면으로 이동할까요?",
            confirmText = "설정으로 이동",
            onConfirm = {
                showPermissionDialog = false
                // 알림 설정 화면 이동
                launchNotificationSettings(context)
            },
            onDismiss = {
                showPermissionDialog = false
            }
        )
    }

    // 로그아웃 확인 다이얼로그
    if (showLogoutDialog) {
        ConfirmDialog(
            message = "정말 로그아웃 하시겠습니까?",
            onConfirm = {
                triggerCooldown()
                viewModel.clearUser()
                showLogoutDialog = false
                navController.navigate(Screen.LoginScreen.route) { popUpTo(0) { inclusive = true } }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // 회원탈퇴 확인 다이얼로그
    if (showWithdrawDialog) {
        ConfirmDialog(
            message = "정말로 탈퇴 하시겠습니까?\n\n모든 데이터가 삭제됩니다.",
            confirmText = "탈퇴하기",
            confirmTextColor = CustomErrorColor,
            confirmContainerColor = MaterialTheme.colorScheme.errorContainer,
            onConfirm = {
                triggerCooldown()
                viewModel.clearUser(alsoDelete = true)
                // 서버에서 삭제여부 확인후 처리필요 (수정필요!!!!!)
                showWithdrawDialog = false
                navController.navigate(Screen.LoginScreen.route) { popUpTo(0) { inclusive = true } }
            },
            onDismiss = { showWithdrawDialog = false }
        )
    }

    // Blocking 화면을 이용해 터치 제어
    if (!canTrigger) {
        BlockingLoadingOverlay(showLoading = false)
    }
}