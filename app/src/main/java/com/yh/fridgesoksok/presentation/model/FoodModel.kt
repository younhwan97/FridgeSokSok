package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.presentation.common.DateFormatter
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
    val dDay: Long = 0
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
