package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodWrapperResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.ReceiptResponse
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FridgeApiService {
    @GET("/")
    suspend fun getFoodList(): FoodWrapperResponse

    @Multipart
    @POST("receipts/upload")
    suspend fun uploadReceiptImage(
        @Part file: MultipartBody.Part
    ): CommonResponse<List<ReceiptResponse>>

    // User Management
    @Headers("Content-Type: application/json")
    @POST("auth/kakao")
    suspend fun createUser(@Body userRequest: UserRequest): CommonResponse<UserResponse>

    // Auth Control
    @GET("auth/validateRefreshToken")
    suspend fun validateUserToken(): CommonResponse<Boolean>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    suspend fun reissueUserToken(): CommonResponse<TokenResponse>
}