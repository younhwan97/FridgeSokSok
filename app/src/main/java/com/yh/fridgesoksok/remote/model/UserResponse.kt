package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class UserResponse(
    val id: Long,
    val token: String,
    val channel: String
) : RemoteMapper<UserEntity> {
    override fun toData(): UserEntity = UserEntity(id, token, channel)
}