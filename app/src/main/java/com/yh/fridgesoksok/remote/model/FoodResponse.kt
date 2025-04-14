package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class FoodResponse(
    val id: Int,
    val name: String,
    val type: Int,
    val count: Int,
    val startDt: String,
    val endDt: String,
) : RemoteMapper<FoodEntity> {
    override fun toData() = FoodEntity(id, name, type, count, startDt, endDt)
}