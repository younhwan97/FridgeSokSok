package com.yh.fridgesoksok.presentation.fridge

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.CreateRecipeFromFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.CreateRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteFoodUseCase
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.common.constants.HttpConstants.HTTP_TOO_MANY_REQUESTS
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_DELETE_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_LOAD_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_UPDATE_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.RECIPE_GENERATION_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.RECIPE_GENERATION_QUOTA_EXCEEDED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.SELECT_INGREDIENTS
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

// +/- 연타를 모아 마지막 값만 서버에 보내기 위한 대기 시간
internal const val COUNT_SYNC_DEBOUNCE_MS = 400L

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val loadUserUseCase: LoadUserUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val createRecipeFromFridgeUseCase: CreateRecipeFromFridgeUseCase
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

    // 사용자에게 보여줄 에러 메시지 (스낵바)
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

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

    // 수량 변경 동기화 상태 (메인 스레드에서만 접근)
    // - countSyncJobs: 음식별로 서버 전송을 기다리는 작업 (새 탭이 오면 대기 중인 작업은 취소)
    // - countSyncTokens: 가장 최근 변경을 식별 (이전 요청 응답이 최신 화면 값을 덮지 않도록)
    // - confirmedFoods: 서버에 반영된 마지막 값 (실패 시 이 값으로 되돌림)
    private val countSyncJobs = mutableMapOf<String, Job>()
    private val countSyncTokens = mutableMapOf<String, Any>()
    private val confirmedFoods = mutableMapOf<String, FoodModel>()

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
                    RecipeGenerationEvent.Error(SELECT_INGREDIENTS)
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
                            HTTP_TOO_MANY_REQUESTS -> RECIPE_GENERATION_QUOTA_EXCEEDED
                            else -> result.message ?: RECIPE_GENERATION_FAILED
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
     * 냉장고 재료 자동 선택 레시피 생성
     * @param onStateChange 상태 변경 시 HomeViewModel에 알림
     */
    fun generateRecipeFromFridge(onStateChange: (RecipeGenerationState) -> Unit) {
        // Loading 상태 알림
        onStateChange(RecipeGenerationState.Loading)

        createRecipeFromFridgeUseCase()
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        // Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)

                        viewModelScope.launch {
                            _recipeGenerationEvent.emit(RecipeGenerationEvent.Success)
                        }
                    }

                    is Resource.Error -> {
                        // Idle 상태로 복귀 알림
                        onStateChange(RecipeGenerationState.Idle)

                        val errorMessage = when (result.status) {
                            HTTP_TOO_MANY_REQUESTS -> RECIPE_GENERATION_QUOTA_EXCEEDED
                            else -> result.message ?: RECIPE_GENERATION_FAILED
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
                    // 서버 전송 대기 중인 수량 변경은 화면 값을 유지
                    val pending = _foods.value.filter { it.id in countSyncTokens }.associateBy { it.id }
                    _foods.value = result.data
                        ?.map { it.toPresentation() }
                        ?.map { pending[it.id] ?: it }
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
                    // 이미 표시 중인 목록이 있으면 유지하고 알림만 표시
                    if (_foods.value.isEmpty()) {
                        _state.value = FridgeState.Error
                    } else {
                        _errorMessage.emit(FOOD_LOAD_FAILED)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 음식 정보 업데이트
     */
    fun updateFood(food: FoodModel) {
        val current = _foods.value.find { it.id == food.id } ?: return
        // 화면에 먼저 반영 (서버 응답을 기다리지 않음)
        confirmedFoods.putIfAbsent(food.id, current)
        replaceFood(food)

        val token = Any()
        countSyncTokens[food.id] = token
        countSyncJobs.remove(food.id)?.cancel()
        countSyncJobs[food.id] = viewModelScope.launch {
            // 연타는 마지막 값 하나로 모아서 전송
            delay(COUNT_SYNC_DEBOUNCE_MS)
            // 전송이 시작되면 새 탭이 와도 요청/결과 처리를 끝까지 마침
            withContext(NonCancellable) {
                syncFood(food.id, token)
            }
        }
    }

    private suspend fun syncFood(foodId: String, token: Any) {
        val latest = _foods.value.find { it.id == foodId } ?: return
        val result = updateFoodUseCase(latest.toDomain()).first { it !is Resource.Loading }
        val isLatestChange = countSyncTokens[foodId] === token

        when (result) {
            is Resource.Success -> {
                val synced = result.data?.toPresentation() ?: latest
                if (isLatestChange) {
                    clearCountSync(foodId)
                    replaceFood(synced)
                } else if (foodId in countSyncTokens) {
                    // 더 최신 변경이 대기 중: 화면 값은 유지하고 되돌릴 기준만 갱신
                    confirmedFoods[foodId] = synced
                }
            }

            is Resource.Error -> {
                // 더 최신 변경이 대기 중이면 그 요청이 최신 값을 다시 보냄
                if (isLatestChange) {
                    confirmedFoods[foodId]?.let { replaceFood(it) }
                    clearCountSync(foodId)
                    _errorMessage.emit(FOOD_UPDATE_FAILED)
                }
            }

            is Resource.Loading -> Unit
        }
    }

    private fun replaceFood(food: FoodModel) {
        _foods.value = _foods.value
            .map { if (it.id == food.id) food else it }
            .sortedBy { it.expiryDate }
    }

    private fun clearCountSync(foodId: String) {
        countSyncTokens.remove(foodId)
        countSyncJobs.remove(foodId)
        confirmedFoods.remove(foodId)
    }

    /**
     * 음식 삭제
     */
    fun deleteFood(food: FoodModel) {
        // 아직 보내지 않은 수량 변경은 삭제할 음식이므로 취소
        countSyncJobs[food.id]?.cancel()
        clearCountSync(food.id)
        executeSingleAction(food.id) {
            deleteFoodUseCase(food.id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val isDeleted = result.data ?: false
                        if (isDeleted) {
                            _foods.value = _foods.value.filterNot { it.id == food.id }
                        } else {
                            _errorMessage.emit(FOOD_DELETE_FAILED)
                        }
                    }

                    is Resource.Error -> _errorMessage.emit(FOOD_DELETE_FAILED)
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
            // id로만 비교
            val existing = selected.find { it.id == food.id }
            if (existing != null) {
                selected - existing
            } else {
                selected + food
            }
        }
    }

    /**
     * 전체 선택
     */
    fun selectAllFoods() {
        _selectedFoods.update { currentSelected ->
            val filteredIds = filteredFoods.value.map { it.id }.toSet()
            val newSelections = filteredFoods.value.filter { food ->
                currentSelected.none { it.id == food.id }
            }
            currentSelected + newSelections.toSet()
        }
    }

    /**
     * 전체 해제
     */
    fun deselectAllFoods() {
        _selectedFoods.update { currentSelected ->
            val filteredIds = filteredFoods.value.map { it.id }.toSet()
            currentSelected.filter { it.id !in filteredIds }.toSet()
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
