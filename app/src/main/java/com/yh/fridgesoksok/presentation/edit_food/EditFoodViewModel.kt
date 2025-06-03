package com.yh.fridgesoksok.presentation.edit_food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.SearchLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EditFoodViewModel @Inject constructor(
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val searchLocalFoodsUseCase: SearchLocalFoodsUseCase
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    fun updateFood(food: FoodModel) {
        updateFoodUseCase(food.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {

                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun onNameInputChanged(query: String) {
        searchLocalFoodsUseCase(query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val results = searchLocalFoodsUseCase(query)
                    _suggestions.value = result.data?.map { it.itemName } ?: emptyList()
                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}