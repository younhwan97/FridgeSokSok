package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.data.model.SummaryFoodEntity

interface RemoteDataSource {

    suspend fun getSummaryFoods(): List<SummaryFoodEntity>
}