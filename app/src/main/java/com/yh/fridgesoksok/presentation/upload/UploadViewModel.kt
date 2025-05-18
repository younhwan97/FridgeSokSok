package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.AddFoodListUseCase
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase,
    private val addFoodListUseCase: AddFoodListUseCase
) : ViewModel() {

    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    var newFoods = _newFoods.asStateFlow()

    fun uploadReceiptImage(bitmap: Bitmap) {
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> {}

                is Resource.Success -> {
                    val receiptList = result.data

                    if (receiptList != null) {
                        val usedIds = _newFoods.value.mapNotNull { it.id.toIntOrNull() }.toSet()
                        var nextId = (0..Int.MAX_VALUE).first { it !in usedIds }

                        val now = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

                        val mappedFoods = receiptList.map { receipt ->
                            val expiryDate = now
                                .atStartOfDay()
                                .plusHours(receipt.shelfLifeHours.toLong())
                                .toLocalDate()

                            FoodModel(
                                id = (nextId++).toString(),
                                fridgeId = "",
                                itemName = receipt.itemName,
                                count = receipt.count,
                                createdAt = now.format(formatter),
                                expiryDate = expiryDate.format(formatter),
                                categoryId = receipt.categoryId,
                            )
                        }

                        _newFoods.value += mappedFoods
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

    fun addFood(newFood: FoodModel) {
        val currentList = _newFoods.value
        val usedIds = currentList.mapNotNull { it.id.toIntOrNull() }.toSet()
        val nextId = (0..Int.MAX_VALUE).first { it !in usedIds }

        val updatedList = (currentList + newFood.copy(id = nextId.toString())).sortedByDescending { it.id }

        _newFoods.value = updatedList
    }

    fun tmp() {
        addFoodListUseCase(_newFoods.value.map { it.toDomain() }).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    //
                }

                is Resource.Error -> {
                    //
                }

                is Resource.Success -> {

                }
            }
        }.launchIn(viewModelScope)
    }
}
