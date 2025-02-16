package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.SummaryFood

data class SummaryFoodModel(
    val id: Int,
    val name: String,
    val startDt: String,
    val endDt: String,
    val foodImageUrl: String,
)

fun SummaryFood.toPresentation() =
    SummaryFoodModel(id, name, startDt, endDt, foodImageUrl)