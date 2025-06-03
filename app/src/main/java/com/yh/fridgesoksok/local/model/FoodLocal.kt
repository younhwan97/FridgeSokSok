package com.yh.fridgesoksok.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yh.fridgesoksok.data.model.ParsedExcelFoodEntity

@Entity(tableName = "FOOD")
data class FoodLocal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val itemName: String,
    val categoryId: Int,
    val hours: Int
)

fun ParsedExcelFoodEntity.toLocal(): FoodLocal =
    FoodLocal(
        id = 0,
        itemName = itemName,
        categoryId = categoryId,
        hours = hours
    )