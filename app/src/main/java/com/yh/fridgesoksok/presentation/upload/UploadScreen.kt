package com.yh.fridgesoksok.presentation.upload

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
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
    val (isBackEnabled, triggerBackCooldown) = rememberBackPressCooldown()

    // Add 후 성공 처리
    LaunchedEffect(Unit) {
        viewModel.addFoodsSuccess.collectLatest {
            navController.popBackStack(Screen.HomeScreen.route, inclusive = false)
        }
    }

    // OCR 이미지 업로드
    LaunchedEffect(receipt) {
        receipt?.let {
            viewModel.uploadReceiptImage(it)
            sharedViewModel.clearReceipt()
        }
    }

    // EditFoodScreen에서 돌아온 편집 결과(editedFood)를 감지
    LaunchedEffect(Unit) {
        snapshotFlow { sharedViewModel.editedFood.value }
            .collectLatest { food ->
                if (food != null) {
                    val exists = viewModel.newFoods.value.any { it.id == food.id }
                    if (exists) {
                        viewModel.updateFood(food.id, food)
                    } else {
                        viewModel.addFood(food)
                    }
                    sharedViewModel.clearEditedFood()
                }
            }
    }

    // 화면
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            UploadTopAppBar(
                isBackEnabled = isBackEnabled,
                onNavigationClick = {
                    triggerBackCooldown()
                    navController.popBackStack()
                },
                onActionClick = {
                    sharedViewModel.clearEditFood()
                    sharedViewModel.setEditFood(null, EditSource.CREATE)
                    navController.navigate(Screen.EditFoodScreen.route)
                }
            )
        },
        bottomBar = { UploadBottomButton { viewModel.addFoods() } }
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
    }
}