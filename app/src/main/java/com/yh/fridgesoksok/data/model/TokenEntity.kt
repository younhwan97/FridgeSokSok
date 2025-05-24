package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Token

data class TokenEntity(
    val accessToken: String?,
    val refreshToken: String?
) : DataMapper<Token> {
    override fun toDomain() = Token(accessToken = accessToken.orEmpty(), refreshToken = refreshToken.orEmpty())
}

