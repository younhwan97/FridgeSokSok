package com.yh.fridgesoksok.data.local

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ParsedExcelFoodEntity

interface LocalFoodDataSource {

    suspend fun insertFoods(foods: List<ParsedExcelFoodEntity>): Boolean

    suspend fun searchFoods(query: String): List<FoodEntity>

    suspend fun getCount(): Int

    suspend fun deleteFoods(): Int

}