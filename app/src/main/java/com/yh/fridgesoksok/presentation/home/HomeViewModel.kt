package com.yh.fridgesoksok.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
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

    private val _uiMode = MutableStateFlow(HomeUiMode.DEFAULT)
    val uiMode = _uiMode.asStateFlow()

    private val _isRecipeGenerationAvailable = MutableStateFlow(false)
    val isRecipeGenerationAvailable = _isRecipeGenerationAvailable.asStateFlow()

    private val _selectAllTrigger = MutableSharedFlow<Unit>()
    val selectAllTrigger = _selectAllTrigger.asSharedFlow()

    private val _deselectAllTrigger = MutableSharedFlow<Unit>()
    val deselectAllTrigger = _deselectAllTrigger.asSharedFlow()

    init {
        // 레시피 생성 API 사용가능여부 셋팅
        _isRecipeGenerationAvailable.value = with(getLocalApiUsageUseCase(ApiUsage.RECIPE_GENERATION.key)) {
            used < max
        }
    }

    fun triggerSelectAll() {
        viewModelScope.launch {
            _selectAllTrigger.emit(Unit)
        }
    }

    fun triggerDeselectAll() {
        viewModelScope.launch {
            _deselectAllTrigger.emit(Unit)
        }
    }

    fun updateToDefaultUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }

    fun updateToRecipeUiMode() {
        _uiMode.value = HomeUiMode.RECIPE_SELECT
    }

    fun resetUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }
}