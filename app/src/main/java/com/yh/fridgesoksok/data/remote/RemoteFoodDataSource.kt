package com.yh.fridgesoksok.data.remote

import android.graphics.Bitmap
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity

interface RemoteFoodDataSource {

    suspend fun addFoodList(foods: List<FoodEntity>): List<FoodEntity>

    suspend fun getFoodList(): List<FoodEntity>

    suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity>
}