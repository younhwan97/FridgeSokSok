package com.yh.fridgesoksok.domain.repository

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun addFoodList(foods: List<Food>): Flow<Resource<List<Food>>>

    fun getFoodList(): Flow<Resource<List<Food>>>

    fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>>
}