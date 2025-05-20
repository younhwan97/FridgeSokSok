package com.yh.fridgesoksok.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    /*
        SharedViewModel은 Home, Upload, Edit, Camera 화면 간에 음식 데이터 및 이미지를 공유하기 위해 사용

        ▸ receipt:
            - Camera → Upload 화면 간 영수증 이미지를 전달하기 위한 상태
            - Upload 진입 시 자동으로 인식되어 OCR 업로드를 수행함

        ▸ newFood:
            - Edit → Upload 흐름에서 사용
            - Upload 화면의 임시 음식 목록에 새 음식 또는 수정된 음식을 반영할 때 사용

        ▸ editFood, editSource, editIndex:
            - Home 또는 Upload → Edit 진입 시 사용
            - 어떤 출처(Home/Upload/Create)에서 Edit에 진입했는지 식별하기 위한 메타데이터
            - Upload에서 진입한 경우, 수정 대상 index 정보를 함께 저장하여 업로드 리스트에서 항목 갱신이 가능하게 함
    */

    // 🔸 Receipt Image (Camera -> Upload)
    private val _receipt = MutableStateFlow<Bitmap?>(null)
    val receipt = _receipt.asStateFlow()

    // 🔸 Edit 후 반영할 데이터
    private val _newFood = MutableStateFlow<FoodModel?>(null)
    val newFood = _newFood.asStateFlow()

    // 🔸 EditSource: Edit 진입 시 출처 (Home / Upload / Create)
    private val _editSource = MutableStateFlow<EditSource?>(null)
    val editSource = _editSource.asStateFlow()

    private val _editIndex = MutableStateFlow<Int?>(null) // Upload 수정 시 index
    val editIndex = _editIndex.asStateFlow()

    private val _editFood = MutableStateFlow<FoodModel?>(null)
    val editFood = _editFood.asStateFlow()

    // --- Setter Functions ---

    fun setImage(bitmap: Bitmap) {
        _receipt.value = bitmap
    }

    fun clearImage() {
        _receipt.value = null
    }

    fun setEditFood(food: FoodModel, source: EditSource, index: Int? = null) {
        _editFood.value = food
        _editSource.value = source
        _editIndex.value = index
    }

    fun clearEditFood() {
        _editFood.value = null
        _editSource.value = null
        _editIndex.value = null
    }

    fun setNewFood(food: FoodModel) {
        _newFood.value = food
    }

    fun clearNewFood() {
        _newFood.value = null
    }
}

enum class EditSource { HOME, UPLOAD, CREATE }