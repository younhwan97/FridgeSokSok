package com.yh.fridgesoksok.presentation.fridge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.DeleteFoodUseCase
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val loadUserUseCase: LoadUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FridgeState>(FridgeState.Loading)
    val state = _state.asStateFlow()

    private val _foods = MutableStateFlow<List<FoodModel>>(emptyList())
    val foods = _foods.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow(Type.All)
    val selectedType = _selectedType.asStateFlow()

    private val _pendingActions = Collections.synchronizedSet(mutableSetOf<String>()) // 중복처리 방지

    fun loadFoods() {
        val fridgeId = loadUserUseCase().defaultFridgeId
        getFoodsUseCase(fridgeId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _foods.value = result.data
                        ?.map { it.toPresentation() }
                        ?.sortedBy { it.expiryDate }
                        .orEmpty()
                    _state.value = FridgeState.Success
                }

                is Resource.Error -> _state.value = FridgeState.Error
                is Resource.Loading -> _state.value = FridgeState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun updateFood(food: FoodModel) {
        executeSingleAction(food.id) {
            updateFoodUseCase(food.toDomain()).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.toPresentation()?.let { updated ->
                            _foods.value = _foods.value
                                .map { if (it.id == updated.id) updated else it }
                                .sortedBy { it.expiryDate }
                        }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    fun deleteFood(food: FoodModel) {
        executeSingleAction(food.id) {
            deleteFoodUseCase(food.id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val isDeleted = result.data ?: false
                        if (isDeleted) _foods.value = _foods.value.filterNot { it.id == food.id }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun executeSingleAction(id: String, block: () -> kotlinx.coroutines.flow.Flow<Resource<*>>) {
        if (!_pendingActions.add(id)) return
        block().onEach {
            if (it !is Resource.Loading) {
                _pendingActions.remove(id)
            }
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedType(type: Type) {
        _selectedType.value = type
    }
}