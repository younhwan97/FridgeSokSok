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
    private val _requestRecipeGeneration = MutableStateFlow(false)
    val requestRecipeGeneration = _requestRecipeGeneration.asStateFlow()

    fun requestRecipeGeneration() {
        _requestRecipeGeneration.value = true
    }

    fun clearRecipeGenerationRequest() {
        _requestRecipeGeneration.value = false
    }
    // endregion

    // region ▸ 레시피 리로드 요청 (RecipeSceen에서 감지)
}

enum class EditSource { HOME, UPLOAD, CREATE }