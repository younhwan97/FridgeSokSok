package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.remote.RemoteMapper

data class FridgeResponse(
    val id: String,
    val fridgeName: String,
    val createdAt: String,
    val foodProducts: List<Food>
) : RemoteMapper<FridgeEntity> {
    override fun toData(): FridgeEntity =
        FridgeEntity(id, fridgeName, createdAt, foodProducts)
}
