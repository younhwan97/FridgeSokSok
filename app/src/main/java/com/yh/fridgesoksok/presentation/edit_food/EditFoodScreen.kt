package com.yh.fridgesoksok.presentation.edit_food

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodBottomButton
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodContent
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodDateSelector
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodNameSuggestion
import com.yh.fridgesoksok.presentation.edit_food.comp.EditFoodTopAppBar
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditFoodScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: EditFoodViewModel = hiltViewModel()
) {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val editSource by sharedViewModel.editSource.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()

    var food by remember {
        mutableStateOf(
            sharedViewModel.editFood.value ?: FoodModel(
                id = "NEW", fridgeId = "NEW", itemName = "",
                expiryDate = LocalDate.now().plusWeeks(2).format(formatter),
                categoryId = Type.Ingredients.id, count = 1, createdAt = ""
            )
        )
    }

    var showDateSelectorDialog by remember { mutableStateOf(false) }
    var isClicking by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    val suggestionOffsetY = remember { mutableFloatStateOf(0f) }

    BackHandler(enabled = suggestions.isNotEmpty()) {
        viewModel.clearSuggestions()
    }

    // Content
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditFoodTopAppBar(
                title = if (editSource == EditSource.HOME) "변경하기" else "식품 추가하기",
                onNavigationClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            EditFoodBottomButton(
                text = if (editSource == EditSource.HOME) "변경하기" else "추가하기",
                onClick = {
                    // 유효성 검사
                    if (food.itemName.isBlank()) {
                        nameError = true
                        return@EditFoodBottomButton
                    }
                    // 중복처리 방지
                    if (!isClicking) {
                        isClicking = true
                        when (editSource) {
                            EditSource.HOME -> viewModel.updateFood(food)
                            EditSource.UPLOAD, EditSource.CREATE -> sharedViewModel.setNewFood(food.copy(fridgeId = "tmp"))
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
                food = food,
                nameError = nameError,
                onFoodChange = { food = it },
                onDateEditRequest = { showDateSelectorDialog = true },
                onSuggestionUpdate = viewModel::onNameInputChanged,
                onSuggestionClear = viewModel::clearSuggestions,
                onSuggestionAnchorPositioned = { coordinates ->
                    suggestionOffsetY.floatValue = coordinates.positionInRoot().y + coordinates.size.height
                }
            )
        }

        if (suggestions.isNotEmpty()) {
            EditFoodNameSuggestion(
                suggestions = suggestions,
                suggestionOffsetY = suggestionOffsetY.floatValue,
                onSuggestionSelected = { suggestion ->
                    food = food.copy(
                        itemName = suggestion.itemName,
                        categoryId = suggestion.categoryId,
                        expiryDate = LocalDateTime.now()
                            .plusHours(suggestion.hours.toLong())
                            .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    )
                    viewModel.clearSuggestions()
                }
            )
        }

        if (showDateSelectorDialog) {
            EditFoodDateSelector(
                selectedDate = LocalDate.parse(food.expiryDate, formatter),
                onDismissRequest = { showDateSelectorDialog = false },
                onConfirm = { food = food.copy(expiryDate = it.format(formatter)) }
            )
        }
    }
}