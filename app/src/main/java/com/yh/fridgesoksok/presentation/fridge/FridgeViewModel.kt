package com.yh.fridgesoksok.presentation.fridge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.CreateRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteFoodUseCase
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val loadUserUseCase: LoadUserUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FridgeState>(FridgeState.Loading)
    val state = _state.asStateFlow()

    private val _foods = MutableStateFlow<List<FoodModel>>(emptyList())
    val foods = _foods.asStateFlow()

    private val _selectedFoods = MutableStateFlow<Set<FoodModel>>(emptySet())
    val selectedFoods = _selectedFoods.asStateFlow()

    private val _recipeGenerationEvent = MutableSharedFlow<RecipeGenerationEvent>()
    val recipeGenerationEvent = _recipeGenerationEvent.asSharedFlow()

    private val _filterQuery = MutableStateFlow("")

    private val _typingQuery = MutableStateFlow("")
    val typingQuery = _typingQuery.asStateFlow()

    private val _selectedType = MutableStateFlow(Type.All)
    val selectedType = _selectedType.asStateFlow()

    val filteredFoods = combine(_foods, _filterQuery, _selectedType) { foods, query, type ->
        foods
            .filter { it.itemName.contains(query, ignoreCase = true) }
            .filter { type == Type.All || it.categoryId == type.id }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _pendingActions = Collections.synchronizedSet(mutableSetOf<String>()) // 중복처리 방지

    /**
     * 레시피 생성
     * @param onStateChange 상태 변경 시 HomeViewModel에 알림
     */
    fun generateRecipe(onStateChange: (RecipeGenerationState) -> Unit) {
        val selectedItems = _selectedFoods.value

        if (selectedItems.isEmpty()) {
            viewModelScope.launch {
                _recipeGenerationEvent.emit(
                    RecipeGenerationEvent.Error("재료를 선택해주세요")
                )
            }
            return
        }

        // ✅ Loading 상태 알림
        onStateChange(RecipeGenerationState.Loading)

        createRecipeUseCase(selectedItems.map { it.toDomain() })
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        // ✅ Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)

                        _selectedFoods.value = emptySet()  // 선택 초기화

                        viewModelScope.launch {
                            _recipeGenerationEvent.emit(RecipeGenerationEvent.Success)
                        }
                    }

                    is Resource.Error -> {
                        // ✅ Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)

                        val errorMessage = when (result.status) {
                            429 -> "AI 레시피 생성 횟수를 모두 소진하였습니다."
                            else -> result.message ?: "레시피 생성에 실패했습니다."
                        }

                        viewModelScope.launch {
                            _recipeGenerationEvent.emit(
                                RecipeGenerationEvent.Error(errorMessage)
                            )
                        }
                    }

                    is Resource.Loading -> {
                        // ✅ Loading 상태 유지 알림
                        onStateChange(RecipeGenerationState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
    }

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

                is Resource.Loading -> {
                    // First Loading
                    if (_foods.value.isEmpty()) {
                        _state.value = FridgeState.Loading
                    }
                }

                is Resource.Error -> {
                    _foods.value = emptyList()
                    _state.value = FridgeState.Error
                }
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

    fun updateTypingQuery(query: String) {
        _typingQuery.value = query
        if (query.isBlank()) _filterQuery.value = ""
    }

    fun updateFilterQuery(query: String) {
        _filterQuery.value = query
    }

    fun updateSelectedType(type: Type) {
        _selectedType.value = type
    }

    fun toggleFoodSelected(food: FoodModel) {
        _selectedFoods.update { selected ->
            if (selected.contains(food)) selected - food else selected + food
        }
    }

    fun selectAllFoods() {
        _selectedFoods.update { currentSelected ->
            currentSelected + filteredFoods.value.toSet()
        }
    }

    fun deselectAllFoods() {
        _selectedFoods.update { currentSelected ->
            currentSelected - filteredFoods.value.toSet()
        }
    }
}

sealed class RecipeGenerationState {
    data object Idle : RecipeGenerationState()
    data object Loading : RecipeGenerationState()
    data object Success : RecipeGenerationState()
    data class Error(val message: String? = null) : RecipeGenerationState()
}

sealed class RecipeGenerationEvent {
    data object Success : RecipeGenerationEvent()
    data class Error(val message: String) : RecipeGenerationEvent()
}