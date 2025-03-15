package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.User

data class UserEntity (
    val id: Long,
    val token: String,
    val channel: String
) : DataMapper<User> {
    override fun toDomain() = User(id, token, channel)
}