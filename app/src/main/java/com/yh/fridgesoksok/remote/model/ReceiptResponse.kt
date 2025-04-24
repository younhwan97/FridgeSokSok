package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class ReceiptResponse(
    val itemName: String,
    val expirationDescription: String,
    val type: String,
    val shelfLifeHours: Int,
    val categoryId: Int
) : RemoteMapper<ReceiptEntity> {
    override fun toData(): ReceiptEntity =
        ReceiptEntity(itemName, expirationDescription, type, shelfLifeHours, categoryId)
}
