package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Receipt

data class ReceiptEntity(
    val name: String,
    val expirationDescription: String,
    val type: String,
    val shelfLifeHours: Int,
    val categoryId: Int
) : DataMapper<Receipt> {
    override fun toDomain() =
        Receipt(name, expirationDescription, type, shelfLifeHours, categoryId)
}
