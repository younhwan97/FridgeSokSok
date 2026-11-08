package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.RecipeEntity
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.toEntity
import com.yh.fridgesoksok.remote.model.toRecipeRequest
import javax.inject.Inject

private const val TAG = "RemoteRecipeData"

class RemoteRecipeDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteRecipeDataSource {

    override suspend fun createRecipe(foods: Collection<FoodEntity>): RecipeEntity {
        val request = foods.toRecipeRequest()
        return apiCall(TAG, "createRecipe", request) {
            fridgeApiService.createRecipe(request).requireData("createRecipe").toEntity()
        }
    }

    override suspend fun createRecipeFromFridge(): RecipeEntity =
        apiCall(TAG, "createRecipeFromFridge") {
            fridgeApiService.createRecipeFromFridge().requireData("createRecipeFromFridge").toEntity()
        }

    override suspend fun getRecipes(): List<RecipeEntity> =
        apiCall(TAG, "getRecipes") {
            fridgeApiService.getRecipes().requireData("getRecipes").map { it.toEntity() }
        }

    override suspend fun deleteRecipe(recipeId: String): Boolean =
        apiCall(TAG, "deleteRecipe", recipeId) {
            fridgeApiService.deleteRecipe(recipeId).requireData("deleteRecipe")
        }
}
