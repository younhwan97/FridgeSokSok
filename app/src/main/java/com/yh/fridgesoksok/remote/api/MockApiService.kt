package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse

class MockApiService : FridgeApiService {

    override suspend fun getSummaryFoods(): SummaryFoodWrapperResponse {
        TODO("Not yet implemented")
    }
}