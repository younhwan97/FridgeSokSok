package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteDataSource {

    override suspend fun getFoodList(): List<FoodEntity> {
        return fridgeApiService.getFoodList().foodList.map { it.toData() }
    }
}