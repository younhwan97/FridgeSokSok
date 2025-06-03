package com.yh.fridgesoksok.data.local

import com.yh.fridgesoksok.data.model.LocalFoodEntity

interface LocalFoodDataSource {

    suspend fun insertFoods(foods: List<LocalFoodEntity>): Boolean

    suspend fun searchFoods(query: String): List<LocalFoodEntity>

    suspend fun getCount(): Int

    suspend fun deleteFoods(): Int

}