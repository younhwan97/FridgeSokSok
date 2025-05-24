package com.yh.fridgesoksok.presentation.fridge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val loadUserUseCase: LoadUserUseCase
) : ViewModel() {

    private val _foods = MutableStateFlow<List<FoodModel>>(emptyList())
    val foods = _foods.asStateFlow()

    init {
        loadFoods()
    }

    private fun loadFoods() {
        val fridgeId = loadUserUseCase().defaultFridgeId

        getFoodsUseCase(fridgeId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    //
                }

                is Resource.Error -> {
                    //
                }

                is Resource.Success -> {
                    _foods.value = result.data!!.map { it.toPresentation() }.sortedBy { it.expiryDate }
                }
            }
        }.launchIn(viewModelScope)
    }
}