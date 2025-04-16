package com.yh.fridgesoksok.domain.repository

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Receipt
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Multipart

interface FoodRepository {

    fun getFoodList(): Flow<Resource<List<Food>>>

    fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>>
}