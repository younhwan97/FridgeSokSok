package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.Period

@Parcelize
data class FoodModel(
    val id: String,
    val fridgeId: String,
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int = 1,
    val createdAt: String,
    val dDay: Long = 0,
    // 영수증에서 유통기한을 찾지 못해 사용자가 직접 확인해야 하는 항목 (업로드 화면 전용)
    val needsExpiryInput: Boolean = false,
    // 제조일 기준 소비기한이라 포장에 적힌 날짜 확인이 필요한 항목 (업로드 화면 전용)
    val needsLabelCheck: Boolean = false
) : Parcelable

fun Food.toPresentation(): FoodModel {
    val expiry = try {
        LocalDate.parse(expiryDate, DateFormatter.yyyyMMdd)
    } catch (e: Exception) {
        LocalDate.now()
    }
    val today = LocalDate.now()
    val period = Period.between(today, expiry)
    val totalDays = period.toTotalMonths() * 30 + period.days

    return FoodModel(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt, totalDays)
}


fun FoodModel.toDomain() =
    Food(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)
