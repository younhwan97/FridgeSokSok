package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.SummaryFood

data class SummaryFoodEntity(
    val id: Int,
    val name: String,
    val startDt: String,
    val endDt: String,
    val foodImageUrl: String,
) : DataMapper<SummaryFood> {
    override fun toDomain() = SummaryFood(id, name, startDt, endDt, foodImageUrl)
}
