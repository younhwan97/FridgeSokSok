package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodModel(
    val id: Int,
    var name: String,
    val type: Int,
    val count: Int = 1,
    val startDt: String,
    val endDt: String,
): Parcelable

fun Food.toPresentation() =
    FoodModel(id, name, type, count, startDt, endDt)