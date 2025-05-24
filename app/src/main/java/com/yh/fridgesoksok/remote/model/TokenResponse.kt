package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.TokenEntity

data class TokenResponse(
    val accessToken: String?,
    val refreshToken: String?
)

fun TokenResponse.toEntity(): TokenEntity {
    return TokenEntity(
        accessToken = accessToken.orEmpty(),
        refreshToken = refreshToken.orEmpty()
    )
}