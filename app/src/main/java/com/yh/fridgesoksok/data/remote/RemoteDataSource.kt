package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.data.model.FoodEntity

interface RemoteDataSource {

    suspend fun getFoodList(): List<FoodEntity>
}