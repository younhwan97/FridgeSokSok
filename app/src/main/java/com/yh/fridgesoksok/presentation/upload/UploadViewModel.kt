package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.usecase.AddFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

private const val DATE_FORMAT = "yyyyMMdd"
private const val ERROR_OCR_FAILED = "영수증을 인식하지 못했어요 :("
private const val ERROR_OCR_LIMIT = "영수증 인식 횟수를 모두 소진하였습니다."
private const val ERROR_ADD_FOODS_FAILED = "식픔 추가에 실패했어요. 다시 시도해주세요."
private const val HTTP_TOO_MANY_REQUESTS = 429

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val addFoodsUseCase: AddFoodsUseCase,
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase,
) : ViewModel() {

    // 화면 상태
    private val _state = MutableStateFlow<UploadState>(UploadState.Success)
    val state = _state.asStateFlow()

    // 업로드할 음식 목록
    private val _newFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    val newFoods = _newFoods.asStateFlow()

    // EditFood에서 추가된 식품 큐 (중복 추가 방지)
    private val processedFoodIds = mutableSetOf<String>()

    // 에러 메시지
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // 식품 추가 완료 이벤트
    private val _foodsAdded = MutableSharedFlow<Unit>()
    val foodsAdded = _foodsAdded.asSharedFlow()

    /**
     * 음식 추가 또는 업데이트
     * - ID가 있고 기존 리스트에 존재하면 → 업데이트
     * - ID가 없거나 리스트에 없으면 → 새로 추가
     */
    fun addOrUpdateFood(food: FoodModel, deduplicationKey: String? = null) {
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
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val receiptList = result.data
                    if (!receiptList.isNullOrEmpty()) {
                        val mappedFoods = mapReceiptToFoodModels(receiptList)
                        _newFoods.update { it + mappedFoods }
                    } else {
                        _errorMessage.emit(ERROR_OCR_FAILED)
                    }
                    _state.value = UploadState.Success
                }

                is Resource.Loading -> {
                    _state.value = UploadState.Loading
                }

                is Resource.Error -> {
                    val errorMsg = if (result.status == HTTP_TOO_MANY_REQUESTS) {
                        ERROR_OCR_LIMIT
                    } else {
                        ERROR_OCR_FAILED
                    }
                    _errorMessage.emit(errorMsg)
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기 위해 Success 세팅
                    _state.value = UploadState.Success
                }
            }
        }.launchIn(viewModelScope)
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
                            _foodsAdded.emit(Unit)
                        }
                    } else {
                        _errorMessage.emit(ERROR_ADD_FOODS_FAILED)
                        _state.value = UploadState.Success
                    }
                }

                is Resource.Loading -> {
                    _state.value = UploadState.Uploading
                }

                is Resource.Error -> {
                    _errorMessage.emit(ERROR_ADD_FOODS_FAILED)
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

    /**
     * 영수증 OCR 결과를 FoodModel 리스트로 변환
     */
    private fun mapReceiptToFoodModels(receiptList: List<Receipt>): List<FoodModel> {
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

        return receiptList.map { item ->
            val expiryDate = now
                .atStartOfDay()
                .plusHours(item.shelfLifeHours.toLong())
                .toLocalDate()

            FoodModel(
                id = UUID.randomUUID().toString(),
                fridgeId = "",
                itemName = item.itemName,
                count = item.count,
                createdAt = now.format(formatter),
                expiryDate = expiryDate.format(formatter),
                categoryId = item.categoryId,
            )
        }
    }
}
