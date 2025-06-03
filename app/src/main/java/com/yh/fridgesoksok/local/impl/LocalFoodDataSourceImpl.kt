package com.yh.fridgesoksok.local.impl

import com.yh.fridgesoksok.data.local.LocalFoodDataSource
import com.yh.fridgesoksok.data.model.LocalFoodEntity
import com.yh.fridgesoksok.local.model.toEntity
import com.yh.fridgesoksok.local.model.toLocal
import com.yh.fridgesoksok.local.room.dao.FoodDao
import javax.inject.Inject

class LocalFoodDataSourceImpl @Inject constructor(
    private val foodDao: FoodDao
) : LocalFoodDataSource {

    override suspend fun insertFoods(foods: List<LocalFoodEntity>): Boolean {
        val result = foodDao.insertAll(foods = foods.map { it.toLocal() })
        return result.size == foods.size
    }

    override suspend fun searchFoods(query: String): List<LocalFoodEntity> {
        return foodDao.searchFoodByKeyword(query).map { it.toEntity() }
    }

    override suspend fun getCount(): Int {
        return foodDao.getCount()
    }

    override suspend fun deleteFoods(): Int {
        return foodDao.deleteFoods()
    }
}