package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import java.time.LocalDate

@Composable
fun EditFoodContent(
    food: FoodModel,
    nameError: Boolean,
    onFoodChange: (FoodModel) -> Unit,
    onDateEditRequest: () -> Unit,
    onSuggestionUpdate: (String) -> Unit,
    onSuggestionClear: () -> Unit,
    onSuggestionAnchorPositioned: (LayoutCoordinates) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        EditFoodLabeledField("식품명", fieldError = nameError) {
            EditFoodName(
                foodName = food.itemName,
                onUserInput = { input ->
                    onFoodChange(food.copy(itemName = input))
                    if (input.isNotBlank()) onSuggestionUpdate(input) else onSuggestionClear()
                },
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onSuggestionClear()
                },
                onPositioned = onSuggestionAnchorPositioned,
            )
        }

        EditFoodLabeledField("유형") {
            EditFoodType(Type.fromId(food.categoryId)) {
                onFoodChange(food.copy(categoryId = it.id))
            }
        }

        EditFoodLabeledField("소비기한") {
            EditFoodDateInput(LocalDate.parse(food.expiryDate, DateFormatter.yyyyMMdd).format(DateFormatter.yyyyMMddDot)) {
                onDateEditRequest()
            }
        }

        EditFoodLabeledField("수량") {
            EditFoodCount(food.count) {
                onFoodChange(food.copy(count = it))
            }
        }

        EditFoodImage(Type.fromId(food.categoryId).iconLarge)
    }
}