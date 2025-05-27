package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.RecipeEntity
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.RecipeResponse
import com.yh.fridgesoksok.remote.model.toEntity
import javax.inject.Inject

class RemoteRecipeDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteRecipeDataSource {

    override suspend fun getRecipes(): List<RecipeEntity> {
        return try {
            Logger.d("RemoteRecipeData", "getRecipes INPUT $")
            val response = CommonResponse(
                status = 200,
                message = "test",
                data = listOf(
                    RecipeResponse(
                        id = "123",
                        recipeName = "",
                        recipeContent = "",
                        createdAt = ""
                    )
                )
            )
            val data = response.data ?: throw IllegalStateException("getRecipes data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteRecipeData", "getRecipes 실패", e)
            throw e
        }
    }
}