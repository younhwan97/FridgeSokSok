package com.yh.fridgesoksok.remote.model

import com.google.gson.annotations.SerializedName
import com.yh.fridgesoksok.data.model.ReceiptEntity

/**
 * 영수증 인식 결과 항목
 * - 매칭된 항목: 유통기한 DB 정보 포함 (신선식품은 id 가 음수)
 * - 미매칭 항목: {"name","count"} 만 내려옴 → itemName 으로 읽고 shelfLifeHours 는 null
 */
data class ReceiptResponse(
    val id: Long?,
    @SerializedName(value = "itemName", alternate = ["name"])
    val itemName: String?,
    val expirationDescription: String?,
    val foodType: String?,
    val shelfLifeHours: Int?,
    val categoryId: Int?,
    val count: Int?,
    val dateBasis: String?
)

fun ReceiptResponse.toEntity(): ReceiptEntity {
    return ReceiptEntity(
        id = id,
        itemName = itemName.orEmpty(),
        expirationDescription = expirationDescription.orEmpty(),
        foodType = foodType.orEmpty(),
        shelfLifeHours = shelfLifeHours,
        categoryId = categoryId ?: 1,
        count = count ?: 1,
        dateBasis = dateBasis
    )
}
