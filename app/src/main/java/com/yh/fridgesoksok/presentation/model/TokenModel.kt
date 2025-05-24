package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.Token

data class TokenModel(
    val accessToken: String,
    val refreshToken: String
) {
    fun isValid(): Boolean = accessToken.isNotBlank() && refreshToken.isNotBlank()
}

fun Token.toPresentation(): TokenModel =
    TokenModel(accessToken, refreshToken)