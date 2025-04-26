package com.yh.fridgesoksok.domain.model

data class Receipt(
    val id: Int,
    val itemName: String,
    val expirationDescription: String,
    val foodType: String,
    val shelfLifeHours: Int,
    val categoryId: Int,
    val count: Int
)
