package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Fridge

data class FridgeEntity(
    val id: String,
    val fridgeName: String,
    val createdAt: String,
    val foodProducts: List<Food>
)

fun FridgeEntity.toDomain(): Fridge {
    return Fridge(
        id = id,
        fridgeName = fridgeName,
        createdAt = createdAt,
        foodProducts = foodProducts
    )
}