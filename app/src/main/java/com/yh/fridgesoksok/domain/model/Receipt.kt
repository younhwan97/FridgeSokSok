package com.yh.fridgesoksok.domain.model

data class Receipt(
    val name: String,
    val expirationDescription: String,
    val type: String,
    val shelfLifeHours: Int,
    val categoryId: Int
)
