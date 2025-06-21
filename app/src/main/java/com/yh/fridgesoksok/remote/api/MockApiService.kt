package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodAddRequest
import com.yh.fridgesoksok.remote.model.FoodResponse
import com.yh.fridgesoksok.remote.model.FridgeResponse
import com.yh.fridgesoksok.remote.model.ReceiptResponse
import com.yh.fridgesoksok.remote.model.RecipeRequest
import com.yh.fridgesoksok.remote.model.RecipeResponse
import com.yh.fridgesoksok.remote.model.UserTmpCreateRequest
import com.yh.fridgesoksok.remote.model.UserCreateRequest
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.UserProfileRequest
import com.yh.fridgesoksok.remote.model.UserProfileRespond
import com.yh.fridgesoksok.remote.model.UserResponse
import com.yh.fridgesoksok.remote.model.UserSettingRequest
import com.yh.fridgesoksok.remote.model.UserSettingRespond
import okhttp3.MultipartBody

// TEST
class MockApiService(
    private val context: Context
) : FridgeApiService {
    /* ********************************************************** */
    /* ************************ Food API ************************ */
    override suspend fun addFoods(fridgeId: String, foodList: List<FoodAddRequest>): CommonResponse<List<FoodResponse>> {
        return CommonResponse(
            message = "Food items added",
            status = 200,
            data = listOf(
                FoodResponse(
                    id = "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                    fridgeId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                    itemName = "Milk",
                    expiryDate = "2023-11-30",
                    categoryId = 2,
                    count = 1,
                    createdAt = "2023-10-15T14:30:00Z"
                ),
                FoodResponse(
                    id = "a9b8c7d6-e5f4-3210-fedc-ba9876543210",
                    fridgeId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                    itemName = "Eggs",
                    expiryDate = "2023-12-10",
                    categoryId = 3,
                    count = 12,
                    createdAt = "2023-10-16T09:15:00Z"
                )
            )
        )
    }

    override suspend fun sendFcmTest(message: String): CommonResponse<String> {
        TODO("Not yet implemented")
    }



    override suspend fun getUserSettings(): CommonResponse<UserSettingRespond> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(userProfileRequest: UserProfileRequest): CommonResponse<UserProfileRespond> {
        TODO("Not yet implemented")
    }
    override suspend fun updateUserSettings(userSettingRequest: UserSettingRequest): CommonResponse<UserSettingRespond> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String): CommonResponse<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipes(): CommonResponse<List<RecipeResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun createRecipe(recipeRequest: RecipeRequest): CommonResponse<RecipeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getFoods(fridgeId: String): CommonResponse<List<FoodResponse>> {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

        context.assets.open("foods.json").use { inputStream ->
            val json = inputStream.readBytes().toString(Charsets.UTF_8)

            val type = object : TypeToken<CommonResponse<List<FoodResponse>>>() {}.type

            return gson.fromJson(json, type)
        }
    }

    override suspend fun updateFood(foodId: String, itemName: String, expiryDate: String, categoryId: Int, count: Int): CommonResponse<FoodResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFood(foodId: String): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    /* ************************************************************* */
    /* ************************ Receipt API ************************ */
    override suspend fun uploadReceiptImage(image: MultipartBody.Part): CommonResponse<List<ReceiptResponse>> {
        TODO("Not yet implemented")
    }

    /* ************************************************************* */
    /* ************************ Receipt API ************************ */
    override suspend fun getFridges(): CommonResponse<List<FridgeResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun createFridge(fridgeName: String): CommonResponse<FridgeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllFridges(): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFridge(fridgeId: String): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFridgeName(fridgeId: String, newName: String): CommonResponse<FridgeResponse> {
        TODO("Not yet implemented")
    }

    /* ************************************************************* */
    /* ************************ User API *************************** */
    override suspend fun createTmpUser(userTmpCreateRequest: UserTmpCreateRequest): CommonResponse<UserResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDefaultFridge(accessToken: String): CommonResponse<FridgeResponse> {
        return CommonResponse(
            message = "User created successfully",
            data = FridgeResponse(
                id = "",
                createdAt = "",
                fridgeName = "",
                foodProducts = emptyList()
            ),
            status = 200
        )
    }

    /* ************************************************************* */
    /* ************************ Oauth API ************************** */
    override suspend fun createUserOnServer(provider: String, userCreateRequest: UserCreateRequest): CommonResponse<UserResponse> {
        return CommonResponse(
            message = "User created successfully",
            data = UserResponse(
                id = "777",
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                username = "mock",
                accountType = "kakao",
                defaultFridgeId = null
            ),
            status = 200
        )
    }

    override suspend fun reissueUserToken(): CommonResponse<TokenResponse> {
        return CommonResponse(
            message = "",
            data = TokenResponse(
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9"
            ),
            status = 200
        )
    }

    override suspend fun validateUserToken(): CommonResponse<Boolean> {
        return CommonResponse(
            message = "",
            data = true,
            status = 200
        )
    }
}