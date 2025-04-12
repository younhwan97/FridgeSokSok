package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.SummaryFood
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryFoodModel(
    val id: Int,
    val name: String,
    val startDt: String,
    val endDt: String,
    val foodImageUrl: String,
): Parcelable

fun SummaryFood.toPresentation() =
    SummaryFoodModel(id, name, startDt, endDt, foodImageUrl)