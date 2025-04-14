package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodWrapperResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface FridgeApiService {
    @GET("/")
    suspend fun getFoodList(): FoodWrapperResponse

    @Headers("Content-Type: application/json")
    @POST("auth/kakao")
    suspend fun createUser(@Body userRequest: UserRequest): CommonResponse<UserResponse>

    @GET("auth/validateRefreshToken")
    suspend fun validateUserToken(): CommonResponse<Boolean>

    @Headers("Content-Type: application/json")
    @POST("auth/refresh")
    suspend fun reissueUserToken(): CommonResponse<TokenResponse>
}