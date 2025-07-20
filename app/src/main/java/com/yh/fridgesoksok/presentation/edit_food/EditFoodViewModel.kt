package com.yh.fridgesoksok.presentation.edit_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.SearchLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.LocalFoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EditFoodViewModel @Inject constructor(
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val searchLocalFoodsUseCase: SearchLocalFoodsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditFoodState>(EditFoodState.Success)
    val state = _state.asStateFlow()

    private val _suggestions = MutableStateFlow<List<LocalFoodModel>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _successUpdateFood = MutableSharedFlow<Unit>()
    val successUpdateFood = _successUpdateFood.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun updateFood(food: FoodModel) {
        if (food.id.isBlank()) return

        updateFoodUseCase(food.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = EditFoodState.Success
                    _successUpdateFood.emit(Unit)
                }

                is Resource.Loading -> {
                    _state.value = EditFoodState.Uploading
                }

                is Resource.Error -> {
                    _errorMessage.emit("변경에 실패했어요. 다시 시도해주세요.")
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기위해 Success 세팅
                    _state.value = EditFoodState.Success
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onNameInputChanged(query: String) {
        if (query.length >= 2) {
            searchLocalFoodsUseCase(query).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _suggestions.value = result.data?.map { it.toPresentation() } ?: emptyList()
                    }

                    is Resource.Error -> _suggestions.value = emptyList()
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}