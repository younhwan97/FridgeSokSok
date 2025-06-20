package com.yh.fridgesoksok.presentation.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.DeleteRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.GetRecipesUseCase
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    val getRecipesUseCase: GetRecipesUseCase,
    val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RecipeState>(RecipeState.Loading)
    val state = _state.asStateFlow()

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _filterQuery = MutableStateFlow("")
    val filterQuery = _filterQuery.asStateFlow()

    private val _typingQuery = MutableStateFlow("")
    val typingQuery = _typingQuery.asStateFlow()

    init {
        getRecipes()
    }

    private fun getRecipes() {
        getRecipesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _recipes.value = result.data!!.map { it.toPresentation() }.sortedByDescending { it.createdAt }
                    _state.value = RecipeState.Success
                }

                is Resource.Error -> _state.value = RecipeState.Error
                is Resource.Loading -> _state.value = RecipeState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun deleteRecipe(recipeId: String) {
        deleteRecipeUseCase(recipeId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isDeleted = result.data ?: false
                    if (isDeleted) getRecipes()
                }

                is Resource.Error -> _state.value = RecipeState.Error
                is Resource.Loading -> _state.value = RecipeState.Loading
            }
        }.launchIn(viewModelScope)
    }

    fun updateTypingQuery(query: String) {
        _typingQuery.value = query
        if (query.isBlank()) _filterQuery.value = ""
    }

    fun updateFilterQuery(query: String) {
        _filterQuery.value = query
    }
}