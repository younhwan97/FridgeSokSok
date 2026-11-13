package com.yh.fridgesoksok.remote.model

// 서버 NotificationRequestDTO: title, body 모두 필수
data class FcmRequest(
    val title: String,
    val body: String
)