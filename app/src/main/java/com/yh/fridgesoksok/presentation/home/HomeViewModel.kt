package com.yh.fridgesoksok.presentation.home

import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.common.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocalApiUsageUseCase: GetLocalApiUsageUseCase
) : ViewModel() {

    private val _uiMode = MutableStateFlow(HomeUiMode.DEFAULT)
    val uiMode = _uiMode.asStateFlow()

    private val _isRecipeGenerationAvailable = MutableStateFlow(false)
    val isRecipeGenerationAvailable = _isRecipeGenerationAvailable.asStateFlow()

    init {
        // 레시피 생성 API 사용가능여부 셋팅
        _isRecipeGenerationAvailable.value = with(getLocalApiUsageUseCase(ApiUsage.RECIPE_GENERATION.key)) {
            used < max
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