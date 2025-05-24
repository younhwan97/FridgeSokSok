package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.ReceiptEntity

data class ReceiptResponse(
    val id: Int?,
    val itemName: String?,
    val expirationDescription: String?,
    val foodType: String?,
    val shelfLifeHours: Int?,
    val categoryId: Int?,
    val count: Int?
)

fun ReceiptResponse.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id ?: -1,
        itemName = itemName.orEmpty(),
        expirationDescription = expirationDescription.orEmpty(),
        foodType = foodType.orEmpty(),
        shelfLifeHours = shelfLifeHours ?: 0,
        categoryId = categoryId ?: 1,
        count = count ?: 1
    )
}