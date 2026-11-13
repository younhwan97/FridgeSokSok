package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodResponse(
    val id: String?,
    val fridgeId: String? = null,
    val itemName: String?,
    val expiryDate: String?,
    val categoryId: Int?,
    val count: Int?,
    // 서버는 등록일을 registerDate 로 내려줌 (fridgeId 는 응답에 없음)
    val registerDate: String?,
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
        createdAt = this.registerDate?.let {
            runCatching {
                LocalDate.parse(it, serverFormatter).format(domainFormatter)
            }.getOrNull()
        } ?: LocalDate.now().format(domainFormatter),
    )
}

