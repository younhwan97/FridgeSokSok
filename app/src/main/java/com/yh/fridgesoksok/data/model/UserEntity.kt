package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.User

data class UserEntity(
    val id: String,
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val accountType: String,
    val defaultFridgeId: String
)

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        accessToken = accessToken,
        refreshToken = refreshToken,
        username = username,
        accountType = accountType,
        defaultFridgeId = defaultFridgeId
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        accountType = accountType,
        refreshToken = refreshToken,
        username = username,
        accessToken = accessToken,
        defaultFridgeId = defaultFridgeId
    )
}