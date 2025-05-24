package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.AddFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val addFoodsUseCase: AddFoodsUseCase,
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase,
) : ViewModel() {

    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    var newFoods = _newFoods.asStateFlow()

    fun uploadReceiptImage(bitmap: Bitmap) {
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val receiptList = result.data

                    if (receiptList != null) {
                        val now = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

                        val mappedFoods = receiptList.map {
                            val expiryDate = now
                                .atStartOfDay()
                                .plusHours(it.shelfLifeHours.toLong())
                                .toLocalDate()

                            FoodModel(
                                id = generateNextId(),
                                fridgeId = "",
                                itemName = it.itemName,
                                count = it.count,
                                createdAt = now.format(formatter),
                                expiryDate = expiryDate.format(formatter),
                                categoryId = it.categoryId,
                            )
                        }

                        _newFoods.update { it + mappedFoods }
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun addFoods() {
        val fridgeId = loadUserUseCase().defaultFridgeId
        addFoodsUseCase(fridgeId, _newFoods.value.map { it.toDomain() }).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val foodModel = result.data?.map { it.toPresentation() }
                    Log.d("tset4444", foodModel.toString())
                    if (foodModel != null && foodModel.size == _newFoods.value.size) {
                        //
                        //Log.d("tset4444", foodModel.toString())
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // --- FoodModel control ---
    fun increaseCount(index: Int) = updateList(index) {
        it.copy(count = it.count + 1)
    }

    fun decreaseCount(index: Int) = updateList(index) {
        if (it.count > 1) it.copy(count = it.count - 1) else it
    }

    fun deleteFood(index: Int) {
        _newFoods.update { current ->
            current.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
        }
    }

    fun addFood(newFood: FoodModel) {
        val newIdFood = newFood.copy(id = generateNextId())
        _newFoods.update { (it + newIdFood).sortedByDescending { food -> food.id } }
    }

    fun updateFood(index: Int, updated: FoodModel) = updateList(index) { updated }

    // --- Private Helpers ---
    private fun generateNextId(): String {
        val usedIds = _newFoods.value.mapNotNull { it.id.toIntOrNull() }.toSet()
        val nextId = (0..Int.MAX_VALUE).first { it !in usedIds }
        return nextId.toString()
    }

    private fun updateList(index: Int, transform: (FoodModel) -> FoodModel) {
        _newFoods.update { current ->
            if (index in current.indices) {
                current.toMutableList().apply {
                    this[index] = transform(this[index])
                }
            } else current
        }
    }
}
