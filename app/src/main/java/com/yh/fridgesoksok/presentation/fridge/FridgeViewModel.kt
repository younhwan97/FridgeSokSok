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

private const val ERROR_EMPTY_SELECTION = "재료를 선택해주세요"
private const val ERROR_RECIPE_LIMIT = "AI 레시피 생성 횟수를 모두 소진하였습니다."
private const val ERROR_RECIPE_FAILED = "레시피 생성에 실패했습니다."
private const val HTTP_TOO_MANY_REQUESTS = 429

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val loadUserUseCase: LoadUserUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase
) : ViewModel() {

    // 화면 상태
    private val _state = MutableStateFlow<FridgeState>(FridgeState.Loading)
    val state = _state.asStateFlow()

    // 전체 음식 목록
    private val _foods = MutableStateFlow<List<FoodModel>>(emptyList())
    val foods = _foods.asStateFlow()

    // 선택된 음식 목록 (레시피 생성용)
    private val _selectedFoods = MutableStateFlow<Set<FoodModel>>(emptySet())
    val selectedFoods = _selectedFoods.asStateFlow()

    // 레시피 생성 완료 이벤트
    private val _recipeGenerationEvent = MutableSharedFlow<RecipeGenerationEvent>()
    val recipeGenerationEvent = _recipeGenerationEvent.asSharedFlow()

    // 검색 필터
    private val _filterQuery = MutableStateFlow("")

    // 사용자 입력 중인 검색어
    private val _typingQuery = MutableStateFlow("")
    val typingQuery = _typingQuery.asStateFlow()

    // 선택된 음식 타입 필터
    private val _selectedType = MutableStateFlow(Type.All)
    val selectedType = _selectedType.asStateFlow()

    // 필터링된 음식 목록
    val filteredFoods = combine(_foods, _filterQuery, _selectedType) { foods, query, type ->
        foods
            .filter { it.itemName.contains(query, ignoreCase = true) }
            .filter { type == Type.All || it.categoryId == type.id }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // 중복 처리 방지
    private val _pendingActions = Collections.synchronizedSet(mutableSetOf<String>())

    /**
     * 레시피 생성
     * @param onStateChange 상태 변경 시 HomeViewModel에 알림
     */
    fun generateRecipe(onStateChange: (RecipeGenerationState) -> Unit) {
        val selectedItems = _selectedFoods.value

        // 유효성 검사
        if (selectedItems.isEmpty()) {
            viewModelScope.launch {
                _recipeGenerationEvent.emit(
                    RecipeGenerationEvent.Error(ERROR_EMPTY_SELECTION)
                )
            }
            return
        }

        // Loading 상태 알림
        onStateChange(RecipeGenerationState.Loading)

        createRecipeUseCase(selectedItems.map { it.toDomain() })
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        // Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)
                        // 선택 초기화
                        _selectedFoods.value = emptySet()

                        viewModelScope.launch {
                            _recipeGenerationEvent.emit(RecipeGenerationEvent.Success)
                        }
                    }

                    is Resource.Error -> {
                        // Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)

                        val errorMessage = when (result.status) {
                            HTTP_TOO_MANY_REQUESTS -> ERROR_RECIPE_LIMIT
                            else -> result.message ?: ERROR_RECIPE_FAILED
                        }

                        viewModelScope.launch {
                            _recipeGenerationEvent.emit(
                                RecipeGenerationEvent.Error(errorMessage)
                            )
                        }
                    }

                    is Resource.Loading -> {
                        // Loading 상태 유지 알림
                        onStateChange(RecipeGenerationState.Loading)
                    }
                }
            }.launchIn(viewModelScope)
    }

    /**
     * 냉장고 음식 목록 로드
     */
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

    /**
     * 음식 정보 업데이트
     */
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

    /**
     * 음식 삭제
     */
    fun deleteFood(food: FoodModel) {
        executeSingleAction(food.id) {
            deleteFoodUseCase(food.id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val isDeleted = result.data ?: false
                        if (isDeleted) {
                            _foods.value = _foods.value.filterNot { it.id == food.id }
                        }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    /**
     * 검색어 입력 업데이트 (타이핑 중)
     */
    fun updateTypingQuery(query: String) {
        _typingQuery.value = query
        if (query.isBlank()) {
            _filterQuery.value = ""
        }
    }

    /**
     * 검색 필터 확정
     */
    fun updateFilterQuery(query: String) {
        _filterQuery.value = query
    }

    /**
     * 음식 타입 필터 변경
     */
    fun updateSelectedType(type: Type) {
        _selectedType.value = type
    }

    /**
     * 음식 선택 토글
     */
    fun toggleFoodSelected(food: FoodModel) {
        _selectedFoods.update { selected ->
            if (selected.contains(food)) selected - food else selected + food
        }
    }

    /**
     * 전체 선택
     */
    fun selectAllFoods() {
        _selectedFoods.update { currentSelected ->
            currentSelected + filteredFoods.value.toSet()
        }
    }

    /**
     * 전체 해제
     */
    fun deselectAllFoods() {
        _selectedFoods.update { currentSelected ->
            currentSelected - filteredFoods.value.toSet()
        }
    }

    /**
     * 중복 요청 방지를 위한 단일 액션 실행
     */
    private fun executeSingleAction(
        id: String,
        block: () -> kotlinx.coroutines.flow.Flow<Resource<*>>
    ) {
        if (!_pendingActions.add(id)) return

        block().onEach {
            if (it !is Resource.Loading) {
                _pendingActions.remove(id)
            }
        }.launchIn(viewModelScope)
    }
}

/**
 * 레시피 생성 상태
 */
sealed class RecipeGenerationState {
    data object Idle : RecipeGenerationState()
    data object Loading : RecipeGenerationState()
    data object Success : RecipeGenerationState()
    data class Error(val message: String? = null) : RecipeGenerationState()
}

/**
 * 레시피 생성 완료 이벤트
 */
sealed class RecipeGenerationEvent {
    data object Success : RecipeGenerationEvent()
    data class Error(val message: String) : RecipeGenerationEvent()
}