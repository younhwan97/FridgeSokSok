package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.presentation.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.checkerframework.checker.guieffect.qual.UI
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // ✅ Receipt 이미지 관리 (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setReceipt(bitmap: Bitmap) { _receipt.value = bitmap }
    fun clearReceipt() { _receipt.value = null }

    // ✅ 레시피 생성 요청 (FridgeScreen에서 감지 → ViewModel 호출)
    private val _recipeGenerationState = MutableStateFlow<RecipeGenerationState>(RecipeGenerationState.Idle)
    val recipeGenerationState = _recipeGenerationState.asStateFlow()

    fun startRecipeGeneration() { _recipeGenerationState.value = RecipeGenerationState.Loading }
    fun resetRecipeGenerationState() { _recipeGenerationState.value = RecipeGenerationState.Idle }

    fun completeRecipeGeneration(success: Boolean, errorMessage: String? = null) {
        _recipeGenerationState.value = if (success) RecipeGenerationState.Success else RecipeGenerationState.Error(errorMessage)
    }
}

sealed class RecipeGenerationState {
    data object Idle : RecipeGenerationState()
    data object Loading : RecipeGenerationState()
    data object Success : RecipeGenerationState()
    data class Error(val message: String? = null) : RecipeGenerationState()
}