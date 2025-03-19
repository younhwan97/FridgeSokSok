package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class UserResponse(
    val id: Long,
    val accessToken: String?,
    val refreshToken: String?,
    val username: String?,
    val accountType: String?
) : RemoteMapper<UserEntity> {
    override fun toData(): UserEntity = UserEntity(id, accessToken, refreshToken, username, accountType)
}