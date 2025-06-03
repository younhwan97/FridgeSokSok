package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.LocalFood

data class LocalFoodModel(
    val itemName: String,
    val categoryId: Int,
    val hours: Int
)

fun LocalFood.toPresentation(): LocalFoodModel =
    LocalFoodModel(
        itemName = itemName,
        categoryId = categoryId,
        hours = hours
    )