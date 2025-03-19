package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.User

data class UserEntity (
    val id: Long,
    val accessToken: String?,
    val refreshToken: String?,
    val username: String?,
    val accountType: String?
) : DataMapper<User> {
    override fun toDomain() = User(id, accessToken, refreshToken, username, accountType)
}