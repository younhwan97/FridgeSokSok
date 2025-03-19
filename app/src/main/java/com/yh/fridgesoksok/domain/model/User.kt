package com.yh.fridgesoksok.domain.model

data class User (
    val id: Long,
    val accessToken: String?,
    val refreshToken: String?,
    val username: String?,
    val accountType: String?
)