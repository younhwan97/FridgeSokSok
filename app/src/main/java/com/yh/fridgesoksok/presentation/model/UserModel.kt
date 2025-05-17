package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.User

data class UserModel(
    val id: Long,
    val accessToken: String?,
    val refreshToken: String?,
    val username: String?,
    val accountType: String?
)

fun User.toPresentation() =
    UserModel(id, accessToken, refreshToken, username, accountType)

fun UserModel.toDomain() =
    User(id, accessToken, refreshToken, username, accountType)