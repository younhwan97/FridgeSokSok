package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.UserEntity

interface RemoteUserDataSource {

    suspend fun createUserToken(channel: Channel): UserEntity

    suspend fun createUser(token: String, username: String): UserEntity

    suspend fun validateUserToken(refreshToken: String): Boolean
}