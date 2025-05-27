package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.data.model.RecipeEntity

interface RemoteRecipeDataSource {

    suspend fun getRecipes(): List<RecipeEntity>
}