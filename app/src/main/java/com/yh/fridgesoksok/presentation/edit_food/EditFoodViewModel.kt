package com.yh.fridgesoksok.presentation.edit_food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.SearchLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_NAME_REQUIRED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_UPDATE_FAILED
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.LocalFoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val MIN_SEARCH_LENGTH = 2
private const val DEFAULT_EXPIRY_WEEKS = 2L
private const val KEY_FOOD = "food"
private const val KEY_IS_EDIT_MODE = "isEditMode"

@HiltViewModel
class EditFoodViewModel @Inject constructor(
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val searchLocalFoodsUseCase: SearchLocalFoodsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 현재 편집 중인 음식
    private val _food = MutableStateFlow<FoodModel?>(null)
    val food = _food.asStateFlow()

    // 편집 모드 여부
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    // 화면 상태
    private val _state = MutableStateFlow<EditFoodState>(EditFoodState.Success)
    val state = _state.asStateFlow()

    // 추천 검색어
    private val _suggestions = MutableStateFlow<List<LocalFoodModel>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    // 에러 메시지
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // 음식 저장 완료 이벤트
    private val _foodSaved = MutableSharedFlow<Unit>()
    val foodSaved = _foodSaved.asSharedFlow()

    // 저장 중복 방지 플래그
    private var isSaving = false

    /**
     * 음식 데이터 초기화
     * @param food 편집할 음식 (null이면 새 음식 추가)
     * @param isEditMode true: 기존 음식 수정, false: 새 음식 추가
     */
    fun initializeFood(food: FoodModel?, isEditMode: Boolean) {
        _isEditMode.value = isEditMode
        _food.value = food ?: createDefaultFood()

        // SavedStateHandle에도 저장 (프로세스 종료 대비)
        savedStateHandle[KEY_FOOD] = _food.value
        savedStateHandle[KEY_IS_EDIT_MODE] = isEditMode
    }

    /**
     * 음식 저장 (서버에 전송 또는 결과 반환)
     */
    fun saveFood() {
        // 중복 저장 방지
        if (isSaving) {
            return
        }

        val currentFood = _food.value ?: return

        // 유효성 검사
        if (currentFood.itemName.isBlank()) {
            viewModelScope.launch {
                _errorMessage.emit(FOOD_NAME_REQUIRED)
            }
            return
        }

        isSaving = true

        if (_isEditMode.value) {
            // 편집 모드: 서버 업데이트
            updateFoodOnServer(currentFood)
        } else {
            // 추가 모드: 결과만 반환
            viewModelScope.launch {
                _foodSaved.emit(Unit)
            }
        }
    }

    /**
     * 음식 정보 업데이트 (UI에서 호출)
     */
    fun updateFoodField(updatedFood: FoodModel) {
        _food.value = updatedFood
        savedStateHandle[KEY_FOOD] = updatedFood
    }

    /**
     * 추천 검색어 업데이트
     */
    fun onNameInputChanged(query: String) {
        if (query.length >= MIN_SEARCH_LENGTH) {
            searchLocalFoodsUseCase(query).onEach { result ->
                _suggestions.value = when (result) {
                    is Resource.Success -> result.data?.map { it.toPresentation() } ?: emptyList()
                    is Resource.Error -> emptyList()
                    is Resource.Loading -> _suggestions.value
                }
            }.launchIn(viewModelScope)
        }
    }

    /**
     * 추천 검색어 초기화
     */
    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    /**
     * 기본 음식 데이터 생성
     */
    private fun createDefaultFood(): FoodModel {
        val today = LocalDate.now()
        val expiryDate = today.plusWeeks(DEFAULT_EXPIRY_WEEKS)

        return FoodModel(
            id = "",
            count = 1,
            fridgeId = "",
            itemName = "",
            categoryId = Type.Meat.id,
            createdAt = today.format(DateFormatter.yyyyMMdd),
            expiryDate = expiryDate.format(DateFormatter.yyyyMMdd)
        )
    }

    /**
     * 서버에 음식 업데이트
     */
    private fun updateFoodOnServer(food: FoodModel) {
        if (food.id.isBlank()) return

        updateFoodUseCase(food.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = EditFoodState.Success
                    _foodSaved.emit(Unit)
                }
                is Resource.Loading -> {
                    _state.value = EditFoodState.Uploading
                }
                is Resource.Error -> {
                    _errorMessage.emit(FOOD_UPDATE_FAILED)
                    // 별도의 에러 스크린이 존재하지 않고, 재처리를 가능하도록 하기 위해 Success 세팅
                    _state.value = EditFoodState.Success
                }
            }
        }.launchIn(viewModelScope)
    }
}