package com.yh.fridgesoksok.domain.model

data class ApiUsage(
    val feature: String,
    val max: Int,
    val used: Int
)
