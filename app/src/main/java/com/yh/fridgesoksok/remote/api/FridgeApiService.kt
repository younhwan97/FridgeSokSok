package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse
import retrofit2.http.GET

interface FridgeApiService {
    @GET("/")
    suspend fun getSummaryFoods(): SummaryFoodWrapperResponse
}