package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.RecipeEntity
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodResponse
import com.yh.fridgesoksok.remote.model.RecipeResponse
import com.yh.fridgesoksok.remote.model.toEntity
import com.yh.fridgesoksok.remote.model.toRecipeRequest
import javax.inject.Inject

class RemoteRecipeDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteRecipeDataSource {

    override suspend fun createRecipe(foods: Collection<FoodEntity>): RecipeEntity {
        return try {
            Logger.d("RemoteRecipeData", "createRecipe INPUT ${foods.toRecipeRequest()}")
            val response = fridgeApiService.createRecipe(foods.toRecipeRequest())
            val data = response.data ?: throw IllegalStateException("createRecipe data(=null)")
            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteRecipeData", "createRecipe 실패", e)
            throw e
        }
    }

    override suspend fun getRecipes(): List<RecipeEntity> {
        return try {
            Logger.d("RemoteRecipeData", "getRecipes")
            val response = fridgeApiService.getRecipes()
            val data = response.data ?: throw IllegalStateException("getRecipes data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteRecipeData", "getRecipes 실패", e)
            throw e
        }
    }
}