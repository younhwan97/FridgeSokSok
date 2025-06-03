package com.yh.fridgesoksok.domain.repository

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun addFoods(fridgeId: String, foods: List<Food>): Flow<Resource<List<Food>>>

    fun getFoods(fridgeId: String): Flow<Resource<List<Food>>>

    fun updateFood(food: Food): Flow<Resource<Food>>

    fun deleteFood(foodId: String): Flow<Resource<Boolean>>

    fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>>

    fun initializeLocalFoods(foods: List<LocalFood>): Flow<Resource<Boolean>>

    fun getCountLocalFoods(): Flow<Resource<Int>>

    fun deleteLocalFoods(): Flow<Resource<Int>>

    fun searchLocalFoods(keyword: String): Flow<Resource<List<LocalFood>>>

}