package com.yh.fridgesoksok.data.remote

import android.graphics.Bitmap
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity

interface RemoteFoodDataSource {

    suspend fun addFoods(fridgeId: String, foods: List<FoodEntity>): List<FoodEntity>

    suspend fun getFoods(fridgeId: String): List<FoodEntity>

    suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity>

    suspend fun updateFood(foodEntity: FoodEntity): FoodEntity
}