package com.yh.fridgesoksok.remote.model

data class CommonResponse<T> (
    val message: String,
    val data: T,
    val status: Int
)