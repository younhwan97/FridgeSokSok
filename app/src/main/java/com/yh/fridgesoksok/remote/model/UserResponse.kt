package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserEntity

data class UserResponse(
    val id: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val username: String?,
    val accountType: String?,
    val defaultFridgeId: String?
)

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        id = id.orEmpty(),
        accessToken = accessToken.orEmpty(),
        refreshToken = refreshToken.orEmpty(),
        username = username.orEmpty(),
        accountType = accountType.orEmpty(),
        defaultFridgeId = defaultFridgeId.orEmpty()
    )
}