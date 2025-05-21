package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity

data class FoodAddRequest(
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int
)

fun FoodEntity.toRequest(): FoodAddRequest {
    return FoodAddRequest(
        itemName, expiryDate, categoryId, count
    )
}