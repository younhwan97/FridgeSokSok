package com.yh.fridgesoksok.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
import com.yh.fridgesoksok.presentation.fridge.RecipeGenerationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocalApiUsageUseCase: GetLocalApiUsageUseCase
) : ViewModel() {

    // 홈 UI 모드 (기본 / 레시피 재료 선택)
    private val _uiMode = MutableStateFlow(HomeUiMode.DEFAULT)
    val uiMode = _uiMode.asStateFlow()

    // 레시피 생성 API 사용 가능 여부
    private val _isRecipeGenerationAvailable = MutableStateFlow(false)
    val isRecipeGenerationAvailable = _isRecipeGenerationAvailable.asStateFlow()

    // 전체 선택 트리거 (FridgeViewModel에게 전달)
    private val _selectAllTrigger = MutableSharedFlow<Unit>()
    val selectAllTrigger = _selectAllTrigger.asSharedFlow()

    // 전체 해제 트리거 (FridgeViewModel에게 전달)
    private val _deselectAllTrigger = MutableSharedFlow<Unit>()
    val deselectAllTrigger = _deselectAllTrigger.asSharedFlow()

    // 레시피 생성 트리거 (FridgeViewModel에게 전달)
    private val _generateRecipeTrigger = MutableSharedFlow<Unit>()
    val generateRecipeTrigger = _generateRecipeTrigger.asSharedFlow()

    // 레시피 생성 상태 (FridgeViewModel에서 보고받음)
    private val _recipeGenerationState = MutableStateFlow<RecipeGenerationState>(RecipeGenerationState.Idle)
    val recipeGenerationState = _recipeGenerationState.asStateFlow()

    init {
        checkRecipeGenerationAvailability()
    }

    /**
     * 전체 선택 트리거
     */
    fun triggerSelectAll() {
        viewModelScope.launch {
            _selectAllTrigger.emit(Unit)
        }
    }

    /**
     * 전체 해제 트리거
     */
    fun triggerDeselectAll() {
        viewModelScope.launch {
            _deselectAllTrigger.emit(Unit)
        }
    }

    /**
     * 기본 UI 모드로 변경
     */
    fun updateToDefaultUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }

    /**
     * 레시피 재료 선택 모드로 변경
     */
    fun updateToRecipeUiMode() {
        _uiMode.value = HomeUiMode.RECIPE_SELECT
    }

    /**
     * UI 모드 리셋 (기본 모드로 복귀)
     */
    fun resetUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }

    /**
     * 레시피 생성 트리거 (FridgeViewModel에게 전달)
     */
    fun triggerGenerateRecipe() {
        viewModelScope.launch {
            _generateRecipeTrigger.emit(Unit)
        }
    }

    /**
     * 레시피 생성 상태 업데이트 (FridgeViewModel이 호출)
     */
    fun updateRecipeGenerationState(state: RecipeGenerationState) {
        _recipeGenerationState.value = state
    }

    /**
     * 레시피 생성 API 사용 가능 여부 확인
     */
    private fun checkRecipeGenerationAvailability() {
        val usage = getLocalApiUsageUseCase(ApiUsage.RECIPE_GENERATION.key)
        _isRecipeGenerationAvailable.value = usage.used < usage.max
    }
}