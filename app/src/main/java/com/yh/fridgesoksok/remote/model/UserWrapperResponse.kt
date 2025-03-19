package com.yh.fridgesoksok.remote.model

import com.google.gson.annotations.SerializedName

data class UserWrapperResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val userResponse: UserResponse,
    @SerializedName("status")
    val status: Int
)