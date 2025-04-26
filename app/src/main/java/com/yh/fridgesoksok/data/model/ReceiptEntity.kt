package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Receipt

data class ReceiptEntity(
    val id: Int,
    val itemName: String,
    val expirationDescription: String,
    val foodType: String,
    val shelfLifeHours: Int,
    val categoryId: Int,
    val count: Int
) : DataMapper<Receipt> {
    override fun toDomain() =
        Receipt(id, itemName, expirationDescription, foodType, shelfLifeHours, categoryId, count)
}
