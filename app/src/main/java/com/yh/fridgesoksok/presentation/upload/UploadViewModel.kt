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
                                name = receipt.itemName,
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

    fun increaseCount(index: Int) {
        val list = _newFoods.value.toMutableList()
        if (index in list.indices) {
            val item = list[index]
            list[index] = item.copy(count = item.count + 1)
            _newFoods.value = list
        }
    }

    fun decreaseCount(index: Int) {
        val list = _newFoods.value.toMutableList()
        if (index in list.indices && list[index].count > 1) {
            val item = list[index]
            list[index] = item.copy(count = item.count - 1)
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
        if (newFoods.value.size < 20) {
            _newFoods.value += FoodModel(
                id = tmpKey++,
                name = "길이가길면이렇게보입니다길이가길면이렇게보입니다길이가길면이렇게보입니다길이가길면이렇게보입니다길이가길면이렇게보입니다길이가길면이렇게보입니다",
                count = 1,
                startDt = "",
                endDt = "20251231",
                type = 3
            )
            _newFoods.value += FoodModel(
                id = tmpKey++,
                name = "dlatl",
                count = 1,
                startDt = "",
                endDt = "20251231",
                type = 4
            )
        }
    }
}
