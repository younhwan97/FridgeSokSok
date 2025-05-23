package com.yh.fridgesoksok.domain.repository

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun addFoods(foods: List<Food>): Flow<Resource<List<Food>>>

    fun getFoods(): Flow<Resource<List<Food>>>

    fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>>

    fun updateFood(food: Food): Flow<Resource<Food>>
}