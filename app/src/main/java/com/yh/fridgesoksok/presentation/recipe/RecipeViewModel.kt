package com.yh.fridgesoksok.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.DeleteRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.GetRecipesUseCase
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    val getRecipesUseCase: GetRecipesUseCase,
    val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RecipeState>(RecipeState.Loading)
    val state = _state.asStateFlow()

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())

    private val _filterQuery = MutableStateFlow("")

    private val _typingQuery = MutableStateFlow("")
    val typingQuery = _typingQuery.asStateFlow()

    val filteredRecipes = combine(_recipes, _filterQuery) { recipes, query ->
        if (query.isBlank()) {
            recipes
        } else {
            recipes.filter {
                it.recipeName.contains(query, ignoreCase = true) ||
                        it.recipeContent.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun getRecipes() {
        getRecipesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _recipes.value = result.data!!.map { it.toPresentation() }.sortedByDescending { it.createdAt }
                    _state.value = RecipeState.Success
                }

                is Resource.Loading -> {
                    // First Loading
                    if (_recipes.value.isEmpty()) {
                        _state.value = RecipeState.Loading
                    }
                }

                is Resource.Error -> {
                    _recipes.value = emptyList()
                    _state.value = RecipeState.Error
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteRecipe(recipeId: String) {
        deleteRecipeUseCase(recipeId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isDeleted = result.data ?: false
                    if (isDeleted) {
                        _recipes.update { current -> current.filterNot { it.id == recipeId } }
                    }
                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
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