package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.SummaryFood
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun getSummaryFoods(): Flow<Resource<List<SummaryFood>>>
}