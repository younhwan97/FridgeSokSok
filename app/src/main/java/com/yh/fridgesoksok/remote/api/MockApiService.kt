package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodRequest
import com.yh.fridgesoksok.remote.model.FoodResponse
import com.yh.fridgesoksok.remote.model.FridgeResponse
import com.yh.fridgesoksok.remote.model.ReceiptResponse
import com.yh.fridgesoksok.remote.model.TmpUserRequest
import com.yh.fridgesoksok.remote.model.TokenRequest
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import java.io.InputStream

// TEST
class MockApiService(
    private val context: Context
) : FridgeApiService {

    override suspend fun createTmpUser(tmpUserRequest: TmpUserRequest): CommonResponse<UserResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createFridge(fridgeName: String): CommonResponse<FridgeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFood(foodId: String): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFridge(fridgeId: String): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFridgeName(fridgeId: String, newName: String): CommonResponse<FridgeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addFoods(fridgeId: String, foodList: List<FoodRequest>): CommonResponse<List<FoodResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFood(foodId: String, itemName: String, expiryDate: String, categoryId: Int, count: Int): CommonResponse<FoodResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllFridges(): CommonResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun getFridges(): CommonResponse<List<FridgeResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDefaultFridge(): CommonResponse<String> {
        return CommonResponse(
            message = "User created successfully",
            data = "123",
            status = 200
        )
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

    override suspend fun uploadReceiptImage(image: MultipartBody.Part): CommonResponse<List<ReceiptResponse>> {
        TODO("Not yet implemented")
    }

    // @POST("auth/kakao")
    override suspend fun createUserOnServer(provider: String, tokenRequest: TokenRequest): CommonResponse<UserResponse> {
        /*
            {
                "message": "User created successfully",
                "data": {
                    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                    "username": "john_doe",
                    "accountType": "temp"
                },
                "status": 200
            }
        */
        return CommonResponse(
            message = "User created successfully",
            data = UserResponse(
                id = 777,
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                username = "mock",
                accountType = "kakao"
            ),
            status = 200
        )
    }

    // @GET("auth/validateRefreshToken")
    override suspend fun validateUserToken(): CommonResponse<Boolean> {
        /*
            {
              "message": "string",
              "data": {},
              "status": 0
            }
         */
        return CommonResponse(
            message = "",
            data = true,
            status = 200
        )
    }

    // @POST("auth/refresh")
    override suspend fun reissueUserToken(): CommonResponse<TokenResponse> {
        /*
            {
              "message": "string",
              "data": {},
              "status": 0
            }
         */
        return CommonResponse(
            message = "",
            data = TokenResponse(
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9",
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9"
            ),
            status = 200
        )
    }
}