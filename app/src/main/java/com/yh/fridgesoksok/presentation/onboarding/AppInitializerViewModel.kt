package com.yh.fridgesoksok.presentation.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.usecase.DeleteLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.GetCountLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.InitializeLocalFoodsFromCsvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AppInitializerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCountLocalFoodsUseCase: GetCountLocalFoodsUseCase,
    private val initializeLocalFoodsFromCsvUseCase: InitializeLocalFoodsFromCsvUseCase,
    private val deleteLocalFoodsUseCase: DeleteLocalFoodsUseCase
) : ViewModel() {

    private val _isInitializationCompleted = MutableStateFlow(false)
    val isInitializationCompleted = _isInitializationCompleted.asStateFlow()

    init {
        /* Local 데이터 생성
        * 1. 검색어 추천을 위해 앱 최초 실행 시 한번만 수행 */
        Logger.i("Onboarding", "로컬데이터 초기화")
        
        checkAndInitialize()
    }

    private fun checkAndInitialize() {
        getCountLocalFoodsUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data == 0) {
                        initializeFoodsFromCsv()
                    } else {
                        Logger.d("Onboarding", "로컬데이터 초기화 스킵")
                        _isInitializationCompleted.value = true
                    }
                }

                is Resource.Error -> {
                    _isInitializationCompleted.value = true
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun initializeFoodsFromCsv() {
        // CSV에서 LocalFood 리스트 추출
        val parsedFoods = parseCsvFromAssets(context)
        // 초기화
        initializeLocalFoodsFromCsvUseCase(parsedFoods).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Logger.d("Onboarding", "로컬데이터 초기화 완료")
                    _isInitializationCompleted.value = true
                }

                is Resource.Error -> {
                    Logger.d("Onboarding", "로컬데이터 초기화 실패")
                    _isInitializationCompleted.value = true
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // TEST용 유즈케이스
    private fun deleteLocalFoods() {
        deleteLocalFoodsUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    initializeFoodsFromCsv()
                }

                is Resource.Error -> {
                    _isInitializationCompleted.value = true
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun parseCsvFromAssets(context: Context): List<LocalFood> {
        val inputStream = context.assets.open("initial.csv")
        return inputStream.bufferedReader().readLines()
            .mapNotNull { line ->
                val tokens = line.split(",")
                if (tokens.size >= 3) {
                    val itemName = tokens[0].trim()
                    val hours = tokens[1].trim().toIntOrNull() ?: return@mapNotNull null
                    val categoryId = tokens[2].trim().toIntOrNull() ?: return@mapNotNull null
                    LocalFood(itemName, categoryId, hours)
                } else null
            }
    }
}