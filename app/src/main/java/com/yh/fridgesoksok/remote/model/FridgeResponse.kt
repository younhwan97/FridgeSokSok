package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.domain.model.Food
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FridgeResponse(
    val id: String?,
    // 서버 필드명은 name, createdAt 은 ISO-8601 일시 (예: 2026-11-13T19:39:22.948296Z)
    val name: String?,
    val createdAt: String?,
    val foodProducts: List<Food>?
)

fun FridgeResponse.toEntity(): FridgeEntity {
    val domainFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return FridgeEntity(
        id = id.orEmpty(),
        fridgeName = name.orEmpty(),
        foodProducts = foodProducts ?: emptyList(),
        createdAt = this.createdAt?.let {
            runCatching {
                LocalDate.parse(it.take(10), serverFormatter).format(domainFormatter)
            }.getOrNull()
        } ?: LocalDate.now().format(domainFormatter)
    )
}