package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.SummaryFoodEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class SummaryFoodResponse(
    val id: Int,
    val name: String,
    val startDt: String,
    val endDt: String,
    val foodImageUrl: String
) : RemoteMapper<SummaryFoodEntity> {
    override fun toData() = SummaryFoodEntity(id, name, startDt, endDt, foodImageUrl)
}