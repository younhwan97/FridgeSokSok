package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.presentation.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // ✅ Receipt 이미지 관리 (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setReceipt(bitmap: Bitmap) { _receipt.value = bitmap }
    fun clearReceipt() { _receipt.value = null }

    // ✅ 진입 상태 (Home / Upload → EditFoodScreen)
    data class EditFoodState(
        val food: FoodModel? = null,
        val source: EditSource? = null
    )

    private val _editFoodState = MutableStateFlow(EditFoodState())
    val editFoodState: StateFlow<EditFoodState> = _editFoodState.asStateFlow()

    fun setEditFood(food: FoodModel?, source: EditSource) { _editFoodState.value = EditFoodState(food, source) }
    fun clearEditFood() { _editFoodState.value = EditFoodState() }

    // ✅ 편집 결과 전달 (EditFoodScreen → UploadScreen)
    private val _editedFood = MutableStateFlow<FoodModel?>(null)
    val editedFood: StateFlow<FoodModel?> = _editedFood.asStateFlow()

    fun setEditedFood(food: FoodModel) { _editedFood.value = food }
    fun clearEditedFood() { _editedFood.value = null }

    // ✅ 전체 선택/해제 요청 (FridgeScreen 내 일회성 트리거 용도)
    private val _selectAllFoodsRequested = MutableStateFlow(false)
    val selectAllFoodsRequested = _selectAllFoodsRequested.asStateFlow()

    fun requestSelectAllFoods() { _selectAllFoodsRequested.value = true }
    fun clearSelectAllFoodsRequest() { _selectAllFoodsRequested.value = false }

    private val _deselectAllFoodsRequested = MutableStateFlow(false)
    val deselectAllFoodsRequested = _deselectAllFoodsRequested.asStateFlow()

    fun requestDeselectAllFoods() { _deselectAllFoodsRequested.value = true }
    fun clearDeselectAllFoodsRequest() { _deselectAllFoodsRequested.value = false }

    // ✅ 레시피 생성 요청 (FridgeScreen에서 감지 → ViewModel 호출)
    private val _recipeGenerationState = MutableStateFlow<RecipeGenerationState>(RecipeGenerationState.Idle)
    val recipeGenerationState = _recipeGenerationState.asStateFlow()

    fun startRecipeGeneration() { _recipeGenerationState.value = RecipeGenerationState.Loading }
    fun resetRecipeGenerationState() { _recipeGenerationState.value = RecipeGenerationState.Idle }

    fun completeRecipeGeneration(success: Boolean, errorMessage: String? = null) {
        _recipeGenerationState.value =
            if (success) RecipeGenerationState.Success else RecipeGenerationState.Error(errorMessage)
    }
}

enum class EditSource { HOME, UPLOAD }

sealed class RecipeGenerationState {
    data object Idle : RecipeGenerationState()
    data object Loading : RecipeGenerationState()
    data object Success : RecipeGenerationState()
    data class Error(val message: String? = null) : RecipeGenerationState()
}