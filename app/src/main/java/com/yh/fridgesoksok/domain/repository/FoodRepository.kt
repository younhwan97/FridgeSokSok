package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun getFoodList(): Flow<Resource<List<Food>>>
}