package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Fridge

data class FridgeModel(
    val id: String,
    val fridgeName: String,
    val createdAt: String,
    val foodProducts: List<Food>
) {
    fun isValid(): Boolean = id.isNotBlank()
}

fun Fridge.toPresentation(): FridgeModel =
    FridgeModel(id, fridgeName, createdAt, foodProducts)