package com.yh.fridgesoksok.presentation.edit_food

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodBottomButton
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodContent
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodDateSelector
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodNameSuggestion
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodTopAppBar
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun EditFoodScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: EditFoodViewModel = hiltViewModel()
) {
    // 편집할 식품 초기화
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

    val suggestions by viewModel.suggestions.collectAsState()
    val suggestionOffsetY = remember { mutableFloatStateOf(0f) }     // 제안 컴포저블 위치

    var showDateSelectorDialog by remember { mutableStateOf(false) } // 달력 열림 여부
    var nameError by remember { mutableStateOf(false) }              // 이름 오류 표시 여부
    var canInteract by remember { mutableStateOf(false) }            // 컴포지션 완료 여부
    var isNavigating by remember { mutableStateOf(false) }           // 중복 네비게이션 방지

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

    // Screen
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditFoodTopAppBar(
                title = if (editFoodState.source == EditSource.HOME) "변경하기" else "식품 추가하기",
                onNavigationClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                    }
                }
            )
        },
        bottomBar = {
            EditFoodBottomButton(
                text = if (editFoodState.source == EditSource.HOME) "변경하기" else "추가하기",
                onClick = {
                    if (editFood.itemName.isBlank()) {
                        nameError = true
                        return@EditFoodBottomButton
                    }
                    if (!isNavigating) {
                        isNavigating = true
                        when (editFoodState.source) {
                            EditSource.HOME -> viewModel.updateFood(editFood)
                            EditSource.UPLOAD -> sharedViewModel.setEditedFood(editFood.copy(fridgeId = "tmp"))
                            else -> Unit
                        }
                        navController.popBackStack()
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
    }
}