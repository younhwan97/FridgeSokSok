package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
) : RemoteMapper<TokenEntity> {
    override fun toData(): TokenEntity = TokenEntity(accessToken, refreshToken)
}