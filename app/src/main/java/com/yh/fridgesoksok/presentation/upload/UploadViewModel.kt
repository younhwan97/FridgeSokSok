package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val addFoodsUseCase: AddFoodsUseCase,
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UploadState>(UploadState.Success)
    val state = _state.asStateFlow()

    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    var newFoods = _newFoods.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _addFoodsSuccess = MutableSharedFlow<Unit>()
    val addFoodsSuccess = _addFoodsSuccess.asSharedFlow()

    fun uploadReceiptImage(bitmap: Bitmap) {
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val receiptList = result.data
                    if (!receiptList.isNullOrEmpty()) {
                        val now = LocalDate.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

                        val mappedFoods = receiptList.map {
                            val expiryDate = now
                                .atStartOfDay()
                                .plusHours(it.shelfLifeHours.toLong())
                                .toLocalDate()

                            FoodModel(
                                id = UUID.randomUUID().toString(),
                                fridgeId = "",
                                itemName = it.itemName,
                                count = it.count,
                                createdAt = now.format(formatter),
                                expiryDate = expiryDate.format(formatter),
                                categoryId = it.categoryId,
                            )
                        }

                        _newFoods.update { it + mappedFoods }
                    } else {
                        _errorMessage.value = "영수증을 인식하지 못했어요 :("
                    }
                    _state.value = UploadState.Success
                }

                is Resource.Loading -> {
                    _state.value = UploadState.Loading
                }

                is Resource.Error -> {
                    _errorMessage.value = "영수증을 인식하지 못했어요 :("
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기위해 Success 세팅
                    _state.value = UploadState.Success
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addFoods() {
        val fridgeId = loadUserUseCase().defaultFridgeId
        addFoodsUseCase(fridgeId, _newFoods.value.map { it.toDomain() }).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val foodModel = result.data?.map { it.toPresentation() }
                    if (foodModel != null) {
                        viewModelScope.launch {
                            _addFoodsSuccess.emit(Unit)
                        }
                    } else {
                        _errorMessage.value = "식픔 추가에 실패했어요. 다시 시도해주세요."
                        _state.value = UploadState.Success
                    }
                }

                is Resource.Loading -> {
                    _state.value = UploadState.Uploading
                }

                is Resource.Error -> {
                    _errorMessage.value = "식픔 추가에 실패했어요. 다시 시도해주세요."
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기위해 Success 세팅
                    _state.value = UploadState.Success
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateCount(id: String, update: (Int) -> Int) {
        _newFoods.update { list ->
            list.map { if (it.id == id) it.copy(count = update(it.count)) else it }
        }
    }

    fun deleteFood(id: String) {
        _newFoods.update { it.filterNot { food -> food.id == id } }
    }

    fun addFood(newFood: FoodModel) {
        val newIdFood = newFood.copy(id = UUID.randomUUID().toString())
        _newFoods.update { it + newIdFood }
    }

    fun updateFood(id: String, updated: FoodModel) {
        _newFoods.update { list ->
            list.map { existing ->
                if (existing.id == id) updated else existing
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
