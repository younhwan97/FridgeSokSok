package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodAddRequest(
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int
)

fun FoodEntity.toRequest(): FoodAddRequest {
    val domainFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return FoodAddRequest(
        itemName = itemName,
        categoryId = categoryId,
        count = count,
        expiryDate = LocalDate.parse(expiryDate, domainFormatter).format(serverFormatter),
    )
}