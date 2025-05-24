package com.yh.fridgesoksok.domain.model

data class User (
    val id: String,
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val accountType: String,
    val defaultFridgeId: String,
)