package com.yh.fridgesoksok.presentation.upload

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.common.util.rememberBackPressCooldown
import com.yh.fridgesoksok.presentation.upload.comp.UploadBottomButton
import com.yh.fridgesoksok.presentation.upload.comp.UploadItemListSection
import com.yh.fridgesoksok.presentation.upload.comp.UploadTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UploadScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val uploadState by viewModel.state.collectAsState()
    val newFoods by viewModel.newFoods.collectAsState()
    val receipt by sharedViewModel.receipt.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val (isBackEnabled, triggerBackCooldown) = rememberBackPressCooldown()

    // Camera -> Upload 이미지 전달 후 OCR 처리
    LaunchedEffect(receipt) {
        receipt?.let {
            // SharedViewModel로 전달된 이미지(Bitmap)를 서버에 업로드하여 OCR 수행
            viewModel.uploadReceiptImage(it)
            // 상태 초기화
            sharedViewModel.clearReceipt()
        }
    }

    // Edit -> Upload 편집결과 감지 및 반영
    LaunchedEffect(Unit) {
        snapshotFlow { sharedViewModel.editedFood.value }
            .collectLatest { food ->
                if (food != null && food.itemName.isNotBlank()) {
                    val exists = food.id.isNotBlank() && viewModel.newFoods.value.any { it.id == food.id }
                    if (exists) {
                        // 기존항목 업데이트
                        viewModel.updateFood(food.id, food)
                    } else {
                        // 신규항목 추가
                        viewModel.addFood(food)
                    }
                    // 상태 초기화
                    sharedViewModel.clearEditedFood()
                }
            }
    }

    // 식품 등록 성공 시 Home으로 이동
    LaunchedEffect(Unit) {
        viewModel.addFoodsSuccess.collectLatest {
            navController.popBackStack(Screen.HomeScreen.route, inclusive = false)
        }
    }

    // 서버처리 실패 시 스낵바 메시징
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // 화면
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            UploadTopAppBar(
                isNavigationEnabled = isBackEnabled,
                onNavigationClick = {
                    triggerBackCooldown()
                    navController.popBackStack()
                },
                onActionClick = {
                    sharedViewModel.clearEditFood()
                    sharedViewModel.setEditFood(null, EditSource.UPLOAD)
                    navController.navigate(Screen.EditFoodScreen.route)
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
                enabled = newFoods.isNotEmpty(),
                onClick = { viewModel.addFoods() }
            )
        }
    ) { innerPadding ->
        UploadItemListSection(
            uploadState = uploadState,
            innerPadding = innerPadding,
            foods = newFoods,
            onEdit = {
                sharedViewModel.clearEditFood()
                sharedViewModel.setEditFood(it, EditSource.UPLOAD)
                navController.navigate(Screen.EditFoodScreen.route)
            },
            onDelete = viewModel::deleteFood,
            onIncrease = { id ->
                viewModel.updateCount(id) { it + 1 }
            },
            onDecrease = { id ->
                viewModel.updateCount(id) { if (it > 1) it - 1 else it }
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