package com.yh.fridgesoksok.domain.model

data class Food(
    val id: String,
    val fridgeId: String,
    val itemName: String,
    val expiryDate: String,
    val categoryId: Int,
    val count: Int,
    val createdAt: String,
)