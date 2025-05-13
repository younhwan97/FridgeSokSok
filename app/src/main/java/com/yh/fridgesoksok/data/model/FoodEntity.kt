package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Food

data class FoodEntity(
    val id: String,
    val fridgeId: String,
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int,
    val createdAt: String,
) : DataMapper<Food> {
    override fun toDomain() = Food(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)
}

fun Food.toEntity(): FoodEntity {
    return FoodEntity(
        id = this.id,
        fridgeId = this.fridgeId,
        itemName = this.itemName,
        expiryDate = this.expiryDate,
        categoryId = this.categoryId,
        count = this.count,
        createdAt = this.createdAt
    )
}