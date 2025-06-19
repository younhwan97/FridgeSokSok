package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    fun createRecipe(foods: Collection<Food>): Flow<Resource<Recipe>>

    fun getRecipes(): Flow<Resource<List<Recipe>>>

    fun deleteRecipe(recipeId: String): Flow<Resource<Boolean>>
}