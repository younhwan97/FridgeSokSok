package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.RecipeEntity
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodResponse
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
                        recipeImageUrl = "https://www.adobe.com/kr/creativecloud/photography/hub/features/media_19243bf806dc1c5a3532f3e32f4c14d44f81cae9f.jpeg?width=750&format=jpeg&optimize=medium",
                        recipeName = "test1",
                        recipeContent = "레시피에 대한 간략한 설명이 들어가는 공간. ",
                        ingredients = listOf(
                            FoodResponse(
                                id = "",
                                createdAt = "",
                                count = 1,
                                itemName = "test 재료",
                                expiryDate = "2025-12-31",
                                categoryId = 1,
                                fridgeId = ""
                            )
                        ),
                        createdAt = "2025-05-01"
                    ),
                    RecipeResponse(
                        id = "1234",
                        recipeImageUrl = "http://119.196.106.135:5000/delayed-image",
                        recipeName = "test2",
                        recipeContent = "레시피에 대한 간략한 설명이 들어가는 공간. ",
                        ingredients = listOf(
                            FoodResponse(
                                id = "",
                                createdAt = "",
                                count = 1,
                                itemName = "test 재료",
                                expiryDate = "2025-12-31",
                                categoryId = 1,
                                fridgeId = ""
                            )
                        ),
                        createdAt = "2025-05-01"
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