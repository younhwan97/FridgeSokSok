package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.domain.model.User

interface RemoteUserDataSource {

    suspend fun createUserToken(channel: Channel): UserEntity

    suspend fun createUser(user: User): UserEntity

    suspend fun validateUserToken(refreshToken: String): Boolean

    suspend fun reissueUserToken(refreshToken: String): TokenEntity
}