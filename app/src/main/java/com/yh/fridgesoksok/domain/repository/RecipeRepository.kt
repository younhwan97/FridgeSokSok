package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    fun getRecipes(): Flow<Resource<List<Recipe>>>
}