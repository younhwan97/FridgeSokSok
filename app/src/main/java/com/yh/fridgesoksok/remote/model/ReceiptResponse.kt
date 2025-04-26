package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class ReceiptResponse(
    val id: Int,
    val itemName: String,
    val expirationDescription: String,
    val foodType: String,
    val shelfLifeHours: Int,
    val categoryId: Int,
    val count: Int
) : RemoteMapper<ReceiptEntity> {
    override fun toData(): ReceiptEntity =
        ReceiptEntity(id, itemName, expirationDescription, foodType, shelfLifeHours, categoryId, count)
}
