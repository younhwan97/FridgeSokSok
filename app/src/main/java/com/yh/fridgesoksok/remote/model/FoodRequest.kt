package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity

data class FoodRequest(
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int
)

fun FoodEntity.toRequest(): FoodRequest {
    return FoodRequest(
        itemName, expiryDate, categoryId, count
    )
}