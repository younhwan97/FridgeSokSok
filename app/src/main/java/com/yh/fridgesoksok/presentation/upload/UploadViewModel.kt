package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.AddFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.common.extension.toResizedJpegBytes
import com.yh.fridgesoksok.presentation.common.constants.HttpConstants.HTTP_TOO_MANY_REQUESTS
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.EXPIRY_INPUT_REQUIRED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_ADD_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.RECEIPT_QUOTA_EXCEEDED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.RECEIPT_RECOGNITION_FAILED
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class UploadViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val addFoodsUseCase: AddFoodsUseCase,
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase,
) : ViewModel() {

    // 화면 상태
    private val _state = MutableStateFlow<UploadState>(UploadState.Success)
    val state = _state.asStateFlow()

    // 인식에 사용한 영수증 이미지 (결과와 비교하는 미리보기용, 축소본)
    private val _receiptPreview = MutableStateFlow<Bitmap?>(null)
    val receiptPreview = _receiptPreview.asStateFlow()

    // 업로드할 음식 목록
    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    val newFoods = _newFoods.asStateFlow()

    // EditFood에서 추가된 식품 큐 (중복 추가 방지, Thread-safe)
    private val processedFoodIds = Collections.synchronizedSet(mutableSetOf<String>())

    // 에러 메시지
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // 식품 추가 완료 이벤트 (추가된 식품 개수)
    private val _foodsAdded = MutableSharedFlow<Int>()
    val foodsAdded = _foodsAdded.asSharedFlow()

    /**
     * 음식 추가 또는 업데이트
     * - ID가 있고 기존 리스트에 존재하면 → 업데이트
     * - ID가 없거나 리스트에 없으면 → 새로 추가
     */
    fun addOrUpdateFood(editedFood: FoodModel, deduplicationKey: String? = null) {
        // 편집 화면을 거친 항목은 사용자가 소비기한을 확인한 것으로 간주
        val food = editedFood.copy(needsExpiryInput = false, needsLabelCheck = false)

        // 중복 추가 방지 (deduplicationKey가 있는 경우)
        if (deduplicationKey != null) {
            if (processedFoodIds.contains(deduplicationKey)) {
                return
            }
            processedFoodIds.add(deduplicationKey)
        }

        _newFoods.update { currentList ->
            val existingIndex = currentList.indexOfFirst {
                it.id == food.id && food.id.isNotBlank()
            }

            if (existingIndex >= 0) {
                // 기존 항목 업데이트
                currentList.toMutableList().apply {
                    set(existingIndex, food)
                }
            } else {
                // 새 항목 추가 (UUID 생성)
                val newFood = if (food.id.isBlank()) {
                    food.copy(id = UUID.randomUUID().toString())
                } else {
                    food
                }
                currentList + newFood
            }
        }
    }

    /**
     * 영수증 이미지 업로드 및 OCR 처리
     */
    fun uploadReceiptImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = UploadState.Loading
            // 리사이즈/인코딩은 메인 스레드를 막지 않도록 Default 디스패처에서 수행
            val (image, preview) = withContext(Dispatchers.Default) {
                bitmap.toResizedJpegBytes() to bitmap.scaledForPreview()
            }
            uploadReceiptImageUseCase(image).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val receiptList = result.data
                        if (!receiptList.isNullOrEmpty()) {
                            val mappedFoods = receiptList.toUploadFoods()
                            _newFoods.update { it + mappedFoods }
                            _receiptPreview.value = preview
                            if (mappedFoods.any { it.needsExpiryInput }) {
                                _errorMessage.emit(EXPIRY_INPUT_REQUIRED)
                            }
                        } else {
                            _errorMessage.emit(RECEIPT_RECOGNITION_FAILED)
                        }
                        _state.value = UploadState.Success
                    }

                    is Resource.Loading -> {
                        _state.value = UploadState.Loading
                    }

                    is Resource.Error -> {
                        val errorMsg = if (result.status == HTTP_TOO_MANY_REQUESTS) {
                            RECEIPT_QUOTA_EXCEEDED
                        } else {
                            RECEIPT_RECOGNITION_FAILED
                        }
                        _errorMessage.emit(errorMsg)
                        // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기 위해 Success 세팅
                        _state.value = UploadState.Success
                    }
                }
            }
        }
    }

    /**
     * 식품 목록 서버에 추가
     */
    fun addFoods() {
        val fridgeId = loadUserUseCase().defaultFridgeId

        addFoodsUseCase(fridgeId, _newFoods.value.map { it.toDomain() }).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val foodModel = result.data?.map { it.toPresentation() }
                    if (foodModel != null) {
                        viewModelScope.launch {
                            _foodsAdded.emit(foodModel.size)
                        }
                    } else {
                        _errorMessage.emit(FOOD_ADD_FAILED)
                        _state.value = UploadState.Success
                    }
                }

                is Resource.Loading -> {
                    _state.value = UploadState.Uploading
                }

                is Resource.Error -> {
                    _errorMessage.emit(FOOD_ADD_FAILED)
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기 위해 Success 세팅
                    _state.value = UploadState.Success
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 특정 음식의 수량 업데이트
     */
    fun updateCount(id: String, update: (Int) -> Int) {
        _newFoods.update { list ->
            list.map { food ->
                if (food.id == id) food.copy(count = update(food.count)) else food
            }
        }
    }

    /**
     * 음식 삭제
     */
    fun deleteFood(id: String) {
        _newFoods.update { list ->
            list.filterNot { food -> food.id == id }
        }
    }
}

// 미리보기는 화면에서 확대해 볼 정도면 충분하므로 긴 변 1600px 로 축소 (메모리 절약)
private const val RECEIPT_PREVIEW_MAX_SIDE = 1600

private fun Bitmap.scaledForPreview(): Bitmap {
    val longSide = maxOf(width, height)
    if (longSide <= RECEIPT_PREVIEW_MAX_SIDE) return this
    val ratio = RECEIPT_PREVIEW_MAX_SIDE.toFloat() / longSide
    return Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
}
