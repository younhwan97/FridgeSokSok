package com.yh.fridgesoksok.domain.model

data class Fridge(
    val id: String,
    val fridgeName: String,
    val createdAt: String,
    val foodProducts: List<Food>
)