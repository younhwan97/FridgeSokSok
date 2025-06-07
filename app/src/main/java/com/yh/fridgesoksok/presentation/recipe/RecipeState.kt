package com.yh.fridgesoksok.presentation.recipe

sealed class RecipeState {
    data object Loading : RecipeState()
    data object Success : RecipeState()
    data object Error : RecipeState()
}