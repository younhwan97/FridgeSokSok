package com.yh.fridgesoksok.local.impl

import com.yh.fridgesoksok.data.local.LocalFoodDataSource
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ParsedExcelFoodEntity
import com.yh.fridgesoksok.local.model.toLocal
import com.yh.fridgesoksok.local.room.dao.FoodDao
import javax.inject.Inject

class LocalFoodDataSourceImpl @Inject constructor(
    private val foodDao: FoodDao
) : LocalFoodDataSource {

    override suspend fun insertFoods(foods: List<ParsedExcelFoodEntity>): Boolean {
        val result = foodDao.insertAll(foods = foods.map { it.toLocal() })
        return result.size == foods.size
    }

    override suspend fun searchFoods(query: String): List<FoodEntity> {
        foodDao.search(query)
        return emptyList<FoodEntity>()
    }
}