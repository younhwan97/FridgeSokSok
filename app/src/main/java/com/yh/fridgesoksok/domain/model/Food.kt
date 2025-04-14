package com.yh.fridgesoksok.domain.model

data class Food(
    val id: Int,
    val name: String,
    val type: Int,
    val count: Int,
    val startDt: String,
    val endDt: String,
)
