package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Food

data class FoodEntity(
    val id: Int,
    val name: String,
    val type: Int,
    val count: Int,
    val startDt: String,
    val endDt: String,
) : DataMapper<Food> {
    override fun toDomain() = Food(id, name, type, count, startDt, endDt)
}
