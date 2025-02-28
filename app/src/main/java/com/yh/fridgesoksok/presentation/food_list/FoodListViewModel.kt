package com.yh.fridgesoksok.presentation.food_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.GetSummaryFoodListUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserTokenUseCase
import com.yh.fridgesoksok.presentation.model.SummaryFoodModel
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(
    private val getSummaryFoodListUseCase: GetSummaryFoodListUseCase,
    private val getUserTokenUseCase: GetUserTokenUseCase
) : ViewModel() {

    private val _summaryFoods = MutableStateFlow<List<SummaryFoodModel>>(emptyList())
    val summaryFood = _summaryFoods.asStateFlow()

    init {
        loadFoods()

        getUserToken()
    }

    private fun loadFoods() {
        getSummaryFoodListUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    //
                }

                is Resource.Error -> {
                    //
                }

                is Resource.Success -> {
                    _summaryFoods.value = result.data!!.map { it.toPresentation() }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserToken() = getUserTokenUseCase()
}