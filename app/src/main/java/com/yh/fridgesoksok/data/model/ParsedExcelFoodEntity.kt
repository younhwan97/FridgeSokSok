package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.local.model.FoodLocal

data class ParsedExcelFoodEntity(
    val itemName: String,
    val categoryId: Int,
    val hours: Int
)