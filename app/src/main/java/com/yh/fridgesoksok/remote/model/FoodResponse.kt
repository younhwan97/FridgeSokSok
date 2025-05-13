package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class FoodResponse(
    val id: String,
    val fridgeId: String,
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int,
    val createdAt: String,
) : RemoteMapper<FoodEntity> {
    override fun toData() = FoodEntity(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)
}