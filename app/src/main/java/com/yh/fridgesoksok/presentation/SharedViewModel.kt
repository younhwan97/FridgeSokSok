package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    // ✅ Receipt 이미지 관리 (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    fun setReceipt(bitmap: Bitmap) { _receipt.value = bitmap }
    fun clearReceipt() { _receipt.value = null }
}