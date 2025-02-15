package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.data.model.SummaryFoodEntity
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteDataSource {

    override suspend fun getSummaryFoods(): List<SummaryFoodEntity> {
        return fridgeApiService.getSummaryFoods().summaryFoods.map { it.toData() }
    }
}