package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.presentation.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // region ▸ Receipt 이미지 (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setReceipt(bitmap: Bitmap) {
        _receipt.value = bitmap
    }

    fun clearReceipt() {
        _receipt.value = null
    }
    // endregion

    // region ▸ Edit 관련 상태 (Home / Upload / Create → EditFoodScreen)
    private val _editFood = MutableStateFlow<FoodModel?>(null)
    val editFood = _editFood.asStateFlow()

    private val _editSource = MutableStateFlow<EditSource?>(null)
    val editSource = _editSource.asStateFlow()

    private val _editIndex = MutableStateFlow<Int?>(null) // Upload 수정 시 index
    val editIndex = _editIndex.asStateFlow()

    fun setEditFood(food: FoodModel, source: EditSource, index: Int? = null) {
        _editFood.value = food
        _editSource.value = source
        _editIndex.value = index
    }

    fun clearEditFood() {
        _editFood.value = null
        _editSource.value = null
        _editIndex.value = null
    }
    // endregion

    // region ▸ UploadScreen에서 Edit 반영용 임시 newFood
    private val _newFood = MutableStateFlow<FoodModel?>(null)
    val newFood = _newFood.asStateFlow()

    fun setNewFood(food: FoodModel) {
        _newFood.value = food
    }

    fun clearNewFood() {
        _newFood.value = null
    }
    // endregion

    // region ▸ 전체 선택/해제 요청 (FridgeScreen 내 일회성 트리거 용도)
    private val _selectAllFoodsRequested = MutableStateFlow(false)
    val selectAllFoodsRequested = _selectAllFoodsRequested.asStateFlow()

    fun requestSelectAllFoods() {
        _selectAllFoodsRequested.value = true
    }

    fun clearSelectAllFoodsRequest() {
        _selectAllFoodsRequested.value = false
    }

    private val _deselectAllFoodsRequested = MutableStateFlow(false)
    val deselectAllFoodsRequested = _deselectAllFoodsRequested.asStateFlow()

    fun requestDeselectAllFoods() {
        _deselectAllFoodsRequested.value = true
    }

    fun clearDeselectAllFoodsRequest() {
        _deselectAllFoodsRequested.value = false
    }
    // endregion

    // region ▸ 레시피 생성 요청 (FridgeScreen에서 감지 → ViewModel 호출)
    private val _recipeGenerationState = MutableStateFlow<RecipeGenerationState>(RecipeGenerationState.Idle)
    val recipeGenerationState = _recipeGenerationState.asStateFlow()

    fun startRecipeGeneration() {
        _recipeGenerationState.value = RecipeGenerationState.Loading
    }

    fun completeRecipeGeneration(success: Boolean, errorMessage: String? = null) {
        _recipeGenerationState.value =
            if (success) RecipeGenerationState.Success else RecipeGenerationState.Error(errorMessage)
    }

    fun resetRecipeGenerationState() {
        _recipeGenerationState.value = RecipeGenerationState.Idle
    }
    // endregion
}

enum class EditSource { HOME, UPLOAD, CREATE }

sealed class RecipeGenerationState {
    data object Idle : RecipeGenerationState()
    data object Loading : RecipeGenerationState()
    data object Success : RecipeGenerationState()
    data class Error(val message: String? = null) : RecipeGenerationState()
}