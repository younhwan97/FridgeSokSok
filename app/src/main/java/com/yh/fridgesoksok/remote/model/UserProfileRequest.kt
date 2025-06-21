package com.yh.fridgesoksok.remote.model

data class UserProfileRequest(
    val username: String?,
    val accountType: String?,
    val notification: Boolean?,
    val fcmToken: String?,
    val oauthId: String?
)