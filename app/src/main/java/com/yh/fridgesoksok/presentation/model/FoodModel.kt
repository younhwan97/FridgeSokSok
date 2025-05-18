package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodModel(
    val id: String,
    val fridgeId: String,
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int = 1,
    val createdAt: String,
    val mode: Int = 1
) : Parcelable {
    val foodMode: FoodMode
        get() = FoodMode.from(mode)
}

fun Food.toPresentation() =
    FoodModel(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)

fun FoodModel.toDomain() =
    Food(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)

enum class FoodMode(val value: Int) {
    ADD(1),   // 추가
    EDIT(2);  // 변경

    companion object {
        fun from(value: Int): FoodMode = entries.find { it.value == value } ?: ADD
    }
}