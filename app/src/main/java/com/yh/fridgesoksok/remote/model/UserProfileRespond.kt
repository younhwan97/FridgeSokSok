package com.yh.fridgesoksok.remote.model

data class UserProfileRespond(
    val id: String,
    val username: String,
    val accountType: String,
    val notification: Boolean,
    val fcmToken: String
)