package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import java.io.InputStream

// TEST
class MockApiService(
    private val context: Context
) : FridgeApiService {

    override suspend fun getSummaryFoods(): SummaryFoodWrapperResponse {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

        context.assets.open("foods.json").use { inputStream: InputStream ->
            val size: Int = inputStream.available()
            val buffer = ByteArray(size).apply {
                inputStream.read(this)
            }
            return gson.fromJson(String(buffer), SummaryFoodWrapperResponse::class.java)
        }
    }

    // @POST("auth/kakao")
    override suspend fun createUser(userRequest: UserRequest): CommonResponse<UserResponse> {
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