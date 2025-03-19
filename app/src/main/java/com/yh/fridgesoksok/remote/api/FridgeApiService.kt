package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserWrapperResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface FridgeApiService {
    @GET("/")
    suspend fun getSummaryFoods(): SummaryFoodWrapperResponse

    @Headers("Content-Type: application/json")
    @POST("auth/kakao")
    suspend fun createUser(@Body userRequest: UserRequest): UserWrapperResponse
}