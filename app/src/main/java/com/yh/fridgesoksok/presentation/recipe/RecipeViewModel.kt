package com.yh.fridgesoksok.presentation.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.GetRecipesUseCase
import com.yh.fridgesoksok.presentation.model.RecipeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes = _recipes.asStateFlow()

    init {
        getRecipes()
    }

    fun getRecipes() {
        getRecipesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.d("test444", result.data.toString())
                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }
}