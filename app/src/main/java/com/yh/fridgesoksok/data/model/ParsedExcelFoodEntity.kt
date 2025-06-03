package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.ParsedExcelFood

data class ParsedExcelFoodEntity(
    val itemName: String,
    val categoryId: Int,
    val hours: Int
)

fun ParsedExcelFood.toEntity(): ParsedExcelFoodEntity =
    ParsedExcelFoodEntity(
        itemName = itemName,
        categoryId = categoryId,
        hours = hours
    )