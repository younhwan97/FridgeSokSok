package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.Token

data class TokenEntity(
    val accessToken: String,
    val refreshToken: String
)

fun TokenEntity.toDomain(): Token {
    return Token(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}