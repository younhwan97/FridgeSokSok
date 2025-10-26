package com.yh.fridgesoksok.presentation.upload

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.common.util.rememberActionCooldown
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.upload.comp.UploadBottomButton
import com.yh.fridgesoksok.presentation.upload.comp.UploadItemListSection
import com.yh.fridgesoksok.presentation.upload.comp.UploadTopAppBar
import kotlinx.coroutines.flow.collectLatest

private const val KEY_FOOD = "food"
private const val KEY_SOURCE = "source"
private const val KEY_EDITED_FOOD = "edited_food"
private const val NAVIGATION_SOURCE_UPLOAD = "UPLOAD"
private const val MIN_COUNT = 1

@Composable
fun UploadScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown(delayMillis = 1400)

    // ViewModel 상태 구독
    val uploadState by viewModel.state.collectAsState()
    val newFoods by viewModel.newFoods.collectAsState() // 업로드할 음식 목록

    // SharedViewModel 상태 구독
    val receipt by sharedViewModel.receipt.collectAsState() // 영수증 이미지 (Camera/Gallery에서 셋팅)

    // Camera/Gallery -> Upload 이미지 전달 후 OCR 처리
    LaunchedEffect(receipt) {
        receipt?.let {
            // SharedViewModel로 전달된 이미지(Bitmap)를 서버에 업로드하여 OCR 수행
            viewModel.uploadReceiptImage(it)
            // 상태 초기화
            sharedViewModel.clearReceipt()
        }
    }

    // 식품 등록 성공 시 Home 화면으로 이동
    LaunchedEffect(Unit) {
        viewModel.foodsAdded.collectLatest {
            navController.popBackStack(Screen.HomeScreen.route, inclusive = false)
        }
    }

    // 서버처리 실패 시 스낵바 메시징
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest {
            snackbarHostState.showSnackbar(it)
        }
    }

    // 화면
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            UploadTopAppBar(
                isNavigationEnabled = canTrigger,
                onNavigationClick = {
                    triggerCooldown()
                    navController.popBackStack()
                },
                onActionClick = {
                    navigateToEditFood(
                        navController = navController,
                        food = null
                    )
                }
            )
        },
        snackbarHost = {
            Snackbar(
                modifier = Modifier.padding(vertical = 16.dp),
                snackBarHostState = snackbarHostState
            )
        },
        bottomBar = {
            UploadBottomButton(
                uploadState = uploadState,
                enabled = newFoods.isNotEmpty() && canTrigger,
                onClick = {
                    viewModel.addFoods()
                }
            )
        }
    ) { innerPadding ->
        UploadItemListSection(
            innerPadding = innerPadding,
            foods = newFoods,
            onEdit = { food ->
                navigateToEditFood(
                    navController = navController,
                    food = food
                )
            },
            onDelete = viewModel::deleteFood,
            onIncrease = { id ->
                viewModel.updateCount(id) { count -> count + 1 }
            },
            onDecrease = { id ->
                viewModel.updateCount(id) { count ->
                    if (count > MIN_COUNT) count - 1 else count
                }
            }
        )

        // 로딩 or 업로딩 상태일 때 Blocking 화면을 이용해 터치 제어
        when (uploadState) {
            UploadState.Loading -> BlockingLoadingOverlay(showLoading = true)
            UploadState.Uploading -> BlockingLoadingOverlay(showLoading = false)
            else -> Unit
        }
    }
}

/**
 * EditFood 화면으로 네비게이션
 */
private fun navigateToEditFood(
    navController: NavController,
    food: FoodModel?
) {
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set(KEY_FOOD, food)
        set(KEY_SOURCE, NAVIGATION_SOURCE_UPLOAD)
    }
    navController.navigate(Screen.EditFoodScreen.route)
}