package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.DateBasis
import com.yh.fridgesoksok.domain.model.Receipt

data class ReceiptEntity(
    val id: Long?,
    val itemName: String,
    val expirationDescription: String,
    val foodType: String,
    val shelfLifeHours: Int?,
    val categoryId: Int,
    val count: Int,
    val dateBasis: String? = null
)

fun ReceiptEntity.toDomain(): Receipt {
    return Receipt(
        id = id,
        itemName = itemName,
        expirationDescription = expirationDescription,
        foodType = foodType,
        shelfLifeHours = shelfLifeHours,
        categoryId = categoryId,
        count = count,
        dateBasis = DateBasis.fromValue(dateBasis)
    )
}
