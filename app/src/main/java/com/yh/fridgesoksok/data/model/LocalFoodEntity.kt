package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.LocalFood

data class LocalFoodEntity(
    val itemName: String,
    val categoryId: Int,
    val hours: Int
)

fun LocalFood.toEntity(): LocalFoodEntity =
    LocalFoodEntity(
        itemName = itemName,
        categoryId = categoryId,
        hours = hours
    )

fun LocalFoodEntity.toDomain(): LocalFood =
    LocalFood(
        itemName = itemName,
        categoryId = categoryId,
        hours = hours
    )