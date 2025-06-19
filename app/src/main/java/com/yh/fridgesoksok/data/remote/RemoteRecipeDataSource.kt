package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.RecipeEntity

interface RemoteRecipeDataSource {

    suspend fun createRecipe(foods: Collection<FoodEntity>): RecipeEntity

    suspend fun getRecipes(): List<RecipeEntity>

    suspend fun deleteRecipe(recipeId: String): Boolean

}