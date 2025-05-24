package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodResponse(
    val id: String?,
    val fridgeId: String?,
    val itemName: String?,
    val expiryDate: String?,
    val categoryId: Int?,
    val count: Int?,
    val createdAt: String?,
)

fun FoodResponse.toEntity(): FoodEntity {
    val domainFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return FoodEntity(
        id = id.orEmpty(),
        fridgeId = fridgeId.orEmpty(),
        itemName = itemName.orEmpty(),
        categoryId = categoryId ?: 1,
        count = count ?: 1,
        expiryDate = this.expiryDate?.let {
            runCatching {
                LocalDate.parse(it, serverFormatter).format(domainFormatter)
            }.getOrNull()
        } ?: LocalDate.now().format(domainFormatter),
        createdAt = this.createdAt?.let {
            runCatching {
                LocalDate.parse(it, serverFormatter).format(domainFormatter)
            }.getOrNull()
        } ?: LocalDate.now().format(domainFormatter),
    )
}

