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
) : Parcelable

fun Food.toPresentation() =
    FoodModel(id, fridgeId, itemName, expiryDate, categoryId, count, createdAt)