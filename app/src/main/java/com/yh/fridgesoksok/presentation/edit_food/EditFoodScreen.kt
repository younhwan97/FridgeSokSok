package com.yh.fridgesoksok.presentation.edit_food

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.common.util.rememberActionCooldown
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodBottomButton
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodContent
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodDateSelector
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodNameSuggestion
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodTopAppBar
import com.yh.fridgesoksok.presentation.model.FoodModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalDateTime

private const val NAVIGATION_SOURCE_HOME = "HOME"
private const val KEY_FOOD = "food"
private const val KEY_SOURCE = "source"
private const val KEY_EDITED_FOOD = "edited_food"

@Composable
fun EditFoodScreen(
    navController: NavController,
    viewModel: EditFoodViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown(delayMillis = 1400)

    // Navigation에서 전달된 데이터 가져오기
    val food = remember { navController.previousBackStackEntry?.savedStateHandle?.get<FoodModel>(KEY_FOOD) }
    val source = remember { navController.previousBackStackEntry?.savedStateHandle?.get<String>(KEY_SOURCE) } // "HOME" or "UPLOAD"

    // ViewModel 상태
    val currentFood by viewModel.food.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val state by viewModel.state.collectAsState() // 화면 상태 (초기 Success, 변경 시 Uploading)
    val suggestions by viewModel.suggestions.collectAsState() // 추천 검색어

    // UI 상태
    var showDateSelectorDialog by remember { mutableStateOf(false) }
    val suggestionOffsetY = remember { mutableFloatStateOf(0f) }

    // 초기화
    LaunchedEffect(Unit) {
        val isEdit = source == NAVIGATION_SOURCE_HOME
        viewModel.initializeFood(food, isEdit)
    }

    // 저장 성공 시 네비게이션
    LaunchedEffect(Unit) {
        viewModel.foodSaved.collectLatest {
            if (isEditMode) {
                // 편집 모드 -> HomeScreen으로 복귀
                navController.popBackStack(Screen.HomeScreen.route, inclusive = false)
            } else {
                // 추가 모드 -> 결과를 UploadScreen에 전달
                currentFood?.let { food ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(KEY_EDITED_FOOD, food)
                }
                navController.popBackStack()
            }
        }
    }

    // 서버 처리 실패 시 스낵바 메시징
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest {
            snackbarHostState.showSnackbar(it)
        }
    }

    // 뒤로가기 처리
    BackHandler(enabled = canTrigger) {
        if (suggestions.isNotEmpty()) {
            viewModel.clearSuggestions()
        } else {
            triggerCooldown()
            navController.popBackStack()
        }
    }

    // 화면
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditFoodTopAppBar(
                title = if (isEditMode) "변경하기" else "식품 추가하기",
                isNavigationEnabled = canTrigger,
                onNavigationClick = {
                    triggerCooldown()
                    navController.popBackStack()
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
            EditFoodBottomButton(
                text = if (isEditMode) "변경하기" else "추가하기",
                editFoodState = state,
                enabled = canTrigger,
                onClick = viewModel::saveFood
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            currentFood?.let { food ->
                EditFoodContent(
                    food = food,
                    nameError = false,
                    onFoodChange = viewModel::updateFoodField,
                    onDateEditRequest = { showDateSelectorDialog = true },
                    onSuggestionUpdate = viewModel::onNameInputChanged,
                    onSuggestionClear = viewModel::clearSuggestions,
                    onSuggestionAnchorPositioned = { coordinates ->
                        suggestionOffsetY.floatValue = coordinates.positionInRoot().y + coordinates.size.height
                    }
                )
            }
        }

        // 추천 검색어
        if (suggestions.isNotEmpty()) {
            EditFoodNameSuggestion(
                suggestions = suggestions,
                suggestionOffsetY = suggestionOffsetY.floatValue,
                onSuggestionSelected = { suggestion ->
                    currentFood?.let { food ->
                        viewModel.updateFoodField(
                            food.copy(
                                itemName = suggestion.itemName,
                                categoryId = suggestion.categoryId,
                                expiryDate = LocalDateTime.now()
                                    .plusHours(suggestion.hours.toLong())
                                    .format(DateFormatter.yyyyMMdd)
                            )
                        )
                    }
                    viewModel.clearSuggestions()
                }
            )
        }

        // 날짜 선택 다이얼로그
        if (showDateSelectorDialog) {
            EditFoodDateSelector(
                selectedDate = LocalDate.parse(currentFood?.expiryDate, DateFormatter.yyyyMMdd),
                onDismissRequest = { showDateSelectorDialog = false },
                onConfirm = { date ->
                    currentFood?.let { food ->
                        viewModel.updateFoodField(
                            food.copy(expiryDate = date.format(DateFormatter.yyyyMMdd))
                        )
                    }
                }
            )
        }

        // Blocking 화면을 이용해 터치 제어
        if (state == EditFoodState.Uploading || !canTrigger) {
            BlockingLoadingOverlay(showLoading = false)
        }
    }
}