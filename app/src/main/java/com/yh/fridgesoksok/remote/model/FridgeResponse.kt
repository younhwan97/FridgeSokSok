package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.domain.model.Food
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FridgeResponse(
    val id: String?,
    val fridgeName: String?,
    val createdAt: String?,
    val foodProducts: List<Food>?
)

fun FridgeResponse.toEntity(): FridgeEntity {
    val domainFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return FridgeEntity(
        id = id.orEmpty(),
        fridgeName = fridgeName.orEmpty(),
        foodProducts = foodProducts ?: emptyList(),
        createdAt = this.createdAt?.let {
            runCatching {
                LocalDate.parse(it, serverFormatter).format(domainFormatter)
            }.getOrNull()
        } ?: LocalDate.now().format(domainFormatter)
    )
}