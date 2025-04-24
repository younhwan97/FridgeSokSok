package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
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

    private var tmpKey = 0

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
                                id = tmpKey++,
                                name = receipt.name,
                                count = receipt.shelfLifeHours,
                                startDt = "20250101",
                                endDt = "20251231",
                                type = 1,
                            )
                        }

                        _newFoods.value += mappedFoods // 기존 목록에 추가
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateFoodName(index: Int, newName: String) =
        updateFood(index) { it.copy(name = newName) }

    fun updateFoodType(index: Int, newType: Int) =
        updateFood(index) { it.copy(type = newType) }

    fun updateFoodCount(index: Int, newCount: Int) =
        updateFood(index) { it.copy(count = newCount) }

    fun updateFoodEndDate(index: Int, endDate: String) =
        updateFood(index) { it.copy(endDt = endDate) }

    private fun updateFood(index: Int, update: (FoodModel) -> FoodModel) {
        val list = _newFoods.value.toMutableList()
        if (index in list.indices) {
            list[index] = update(list[index])
            _newFoods.value = list
        }
    }

    fun deleteFood(index: Int) {
        val list = _newFoods.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _newFoods.value = list
        }
    }

    fun insertEmptyFood() {
        if (newFoods.value.size < 20)
            _newFoods.value += FoodModel(
                id = tmpKey++,
                name = "",
                count = 1,
                startDt = "",
                endDt = "",
                type = 20
            )
    }
}
