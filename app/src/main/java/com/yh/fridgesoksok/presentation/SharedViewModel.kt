package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.common.SessionEvents
import com.yh.fridgesoksok.presentation.model.RecipeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    sessionEvents: SessionEvents
) : ViewModel() {
    // ✅ 세션 만료 이벤트 (리프레시 토큰 만료 → 로그인 화면 이동)
    val sessionExpired = sessionEvents.expired

    // ✅ Receipt 이미지 관리 (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setReceipt(bitmap: Bitmap) { _receipt.value = bitmap }
    fun clearReceipt() { _receipt.value = null }

    // ✅ 선택된 레시피 관리 (Recipe -> RecipeDetail)
    private val _selectedRecipe = MutableStateFlow<RecipeModel?>(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    fun setSelectedRecipe(recipe: RecipeModel) { _selectedRecipe.value = recipe }
    fun clearSelectedRecipe() { _selectedRecipe.value = null }

    override fun onCleared() {
        super.onCleared()
        // Bitmap 메모리 해제 (메모리 누수 방지)
        _receipt.value?.recycle()
        _receipt.value = null
    }
}