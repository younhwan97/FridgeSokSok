package com.yh.fridgesoksok.presentation.edit_food

sealed class EditFoodState {
    data object Loading : EditFoodState()
    data object Success : EditFoodState()
    data object Error : EditFoodState()
    data object Uploading : EditFoodState()
}