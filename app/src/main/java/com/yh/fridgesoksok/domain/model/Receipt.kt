package com.yh.fridgesoksok.domain.model

/**
 * 영수증 인식 결과
 * @param id 유통기한 DB 의 id (신선식품은 음수, 미매칭 항목은 null)
 * @param shelfLifeHours 보관 가능 시간. null 이면 유통기한 정보를 찾지 못한 항목(사용자 입력 필요)
 * @param dateBasis 보관 기간 근거. LABEL 은 제조일 기준이라 구입일로 계산하면 실제보다 늦어질 수 있음
 */
data class Receipt(
    val id: Long?,
    val itemName: String,
    val expirationDescription: String,
    val foodType: String,
    val shelfLifeHours: Int?,
    val categoryId: Int,
    val count: Int,
    val dateBasis: DateBasis? = null
)
