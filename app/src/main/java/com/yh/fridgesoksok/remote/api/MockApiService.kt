package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse

class MockApiService(
    private val context: Context
) : FridgeApiService {

    override suspend fun getSummaryFoods(): SummaryFoodWrapperResponse {
        TODO("Not yet implemented")
    }
}