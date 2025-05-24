package com.yh.fridgesoksok.remote.model

import com.google.gson.annotations.SerializedName

data class CommonResponse<T> (
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?,
    @SerializedName("status")
    val status: Int
)