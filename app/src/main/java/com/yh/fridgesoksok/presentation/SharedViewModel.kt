package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.presentation.model.FoodMode
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setImage(bitmap: Bitmap) {
        _receipt.value = bitmap
    }

    fun clearImage() {
        _receipt.value = null
    }

    private val _editFood = MutableStateFlow(
        FoodModel(
            id = "",
            fridgeId = "",
            itemName = "",
            expiryDate = LocalDate.now().plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            categoryId = Type.Ingredients.id,
            count = 1,
            createdAt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
            mode = FoodMode.ADD.value
        )
    )
    val editFood = _editFood.asStateFlow()

    fun setEditFood(food: FoodModel) {
        _editFood.value = food
    }

    fun clearEditFood() {
        _editFood.value = FoodModel(
            id = "",
            fridgeId = "",
            itemName = "",
            expiryDate = LocalDate.now().plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            categoryId = Type.Ingredients.id,
            count = 1,
            createdAt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
            mode = FoodMode.ADD.value
        )
    }
}