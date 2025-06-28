package com.yh.fridgesoksok.presentation.edit_food

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
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
import com.yh.fridgesoksok.presentation.model.Type
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun EditFoodScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: EditFoodViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDateSelectorDialog by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var canInteract by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val (canTrigger, triggerCooldown) = rememberActionCooldown()

    // 편집 대상
    val editFoodState by sharedViewModel.editFoodState.collectAsState()
    var editFood by remember {
        mutableStateOf(
            editFoodState.food ?: FoodModel(
                id = "",
                count = 1,
                fridgeId = "",
                itemName = "",
                categoryId = Type.Ingredients.id,
                createdAt = LocalDate.now().format(DateFormatter.yyyyMMdd),
                expiryDate = LocalDate.now().plusWeeks(2).format(DateFormatter.yyyyMMdd)
            )
        )
    }

    // 추천 검색어
    val suggestions by viewModel.suggestions.collectAsState()
    val suggestionOffsetY = remember { mutableFloatStateOf(0f) }

    // 컴포지션 완료 후 인터랙션 허용
    LaunchedEffect(Unit) {
        withFrameNanos { canInteract = true }
    }

    // 뒤로가기 시 검색어 제안 닫기
    BackHandler(enabled = suggestions.isNotEmpty()) {
        viewModel.clearSuggestions()
    }

    // 화면 제거 시 상태 초기화
    DisposableEffect(Unit) {
        onDispose {
            showDateSelectorDialog = false
            nameError = false
            canInteract = false
            viewModel.clearSuggestions()
        }
    }

    // 식품 업데이트 성공 시 Home으로 이동
    LaunchedEffect(Unit) {
        viewModel.updateFoodSuccess.collectLatest {
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditFoodTopAppBar(
                title = if (editFoodState.source == EditSource.HOME) "변경하기" else "식품 추가하기",
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
                text = if (editFoodState.source == EditSource.HOME) "변경하기" else "추가하기",
                editFoodState = state,
                enabled = canTrigger,
                onClick = {
                    // 입력값 유효성 검증
                    if (editFood.itemName.isBlank()) {
                        nameError = true
                        return@EditFoodBottomButton
                    }
                    // 소스에 따른 분기처리
                    if (editFoodState.source == EditSource.UPLOAD) {
                        // 중복처리 방지
                        triggerCooldown()
                        // SharedViewModel 편집결과 셋팅
                        sharedViewModel.setEditedFood(editFood)
                        navController.popBackStack()
                    } else if (editFoodState.source == EditSource.HOME) {
                        // Uploading 상태일 때 Blocking 화면이 존재하기 때문에, 별도 중복처리 방지 트리거는 생략
                        // 트리거를 통한 중복제어는 일시적 제어인 반면에 Uploding은 서버응답을 기다리는 화면 고유의 상태이기 때문에 다른 방식으로 처리
                        viewModel.updateFood(editFood)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            EditFoodContent(
                food = editFood,
                nameError = nameError,
                onFoodChange = { editFood = it },
                onDateEditRequest = { if (canInteract) showDateSelectorDialog = true },
                onSuggestionUpdate = viewModel::onNameInputChanged,
                onSuggestionClear = viewModel::clearSuggestions,
                onSuggestionAnchorPositioned = { coordinates ->
                    suggestionOffsetY.floatValue = coordinates.positionInRoot().y + coordinates.size.height
                }
            )
        }

        if (suggestions.isNotEmpty() && canInteract) {
            EditFoodNameSuggestion(
                suggestions = suggestions,
                suggestionOffsetY = suggestionOffsetY.floatValue,
                onSuggestionSelected = { suggestion ->
                    editFood = editFood.copy(
                        itemName = suggestion.itemName,
                        categoryId = suggestion.categoryId,
                        expiryDate = LocalDateTime.now()
                            .plusHours(suggestion.hours.toLong())
                            .format(DateFormatter.yyyyMMdd)
                    )
                    viewModel.clearSuggestions()
                }
            )
        }

        if (showDateSelectorDialog && canInteract) {
            EditFoodDateSelector(
                selectedDate = LocalDate.parse(editFood.expiryDate, DateFormatter.yyyyMMdd),
                onDismissRequest = { showDateSelectorDialog = false },
                onConfirm = { editFood = editFood.copy(expiryDate = it.format(DateFormatter.yyyyMMdd)) }
            )
        }

        // Uploading 상태일 때, Blocking 화면을 이용해 터치 제어
        if (state == EditFoodState.Uploading) {
            BlockingLoadingOverlay(showLoading = false)
        }
    }
}