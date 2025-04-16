package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase
) : ViewModel() {

    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    var newFoods = _newFoods.asStateFlow()

    fun uploadReceiptImage(bitmap: Bitmap) {
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    //
                }

                is Resource.Error -> {
                    //
                }

                is Resource.Success -> {
                    val receiptList = result.data

                    if (receiptList != null) {
                        // Receipt → FoodModel 매핑
                        val mappedFoods = receiptList.map { receipt ->
                            FoodModel(
                                id = -1,
                                name = receipt.name,
                                count = receipt.count.toString(),
                                startDt = "20250101",
                                endDt = "20251231",
                                type = 1,
                            )
                        }

                        _newFoods.value = _newFoods.value + mappedFoods // 기존 목록에 추가

                        Log.d("test4", newFoods.toString())
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateFoodName(index: Int, newName: String) {
        val currentList = _newFoods.value.toMutableList()
        val oldFood = currentList[index]
        currentList[index] = oldFood.copy(name = newName)
        _newFoods.value = currentList
    }

    fun updateFoodCategory(index: Int, newCategory: Int) {
        val updatedList = _newFoods.value.toMutableList()
        updatedList[index] = updatedList[index].copy(type = newCategory)
        _newFoods.value = updatedList
    }

    fun updateFoodCount(index: Int, text: String) {
        val updated = _newFoods.value.toMutableList()
        updated[index] = updated[index].copy(
            count = text
        )
        _newFoods.value = updated
    }

    fun insertEmptyFood() {
        if (newFoods.value.size < 20)
            _newFoods.value += FoodModel(id = -1, name = "", count = "", startDt = "", endDt = "", type = 20)
    }
}
