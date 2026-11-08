package com.yh.fridgesoksok.presentation.upload

import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.model.FoodModel
import java.time.LocalDate
import java.util.UUID

/**
 * 영수증 인식 결과를 업로드 화면의 식품 목록으로 변환
 * - 이름조차 읽지 못한 항목은 제외
 * - 유통기한 정보가 없는 항목(미매칭)은 오늘 날짜로 두고 사용자 확인 필요로 표시
 */
internal fun List<Receipt>.toUploadFoods(today: LocalDate = LocalDate.now()): List<FoodModel> =
    filter { it.itemName.isNotBlank() }
        .map { item ->
            val shelfLifeHours = item.shelfLifeHours
            val expiryDate = if (shelfLifeHours == null) {
                today
            } else {
                today.atStartOfDay().plusHours(shelfLifeHours.toLong()).toLocalDate()
            }

            FoodModel(
                id = UUID.randomUUID().toString(),
                fridgeId = "",
                itemName = item.itemName,
                count = item.count,
                createdAt = today.format(DateFormatter.yyyyMMdd),
                expiryDate = expiryDate.format(DateFormatter.yyyyMMdd),
                categoryId = item.categoryId,
                needsExpiryInput = shelfLifeHours == null,
            )
        }
