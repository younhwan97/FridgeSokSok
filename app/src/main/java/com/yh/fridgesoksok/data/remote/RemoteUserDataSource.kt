package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.domain.model.User

interface RemoteUserDataSource {

    suspend fun createUserOnChannel(channel: Channel): UserEntity

    suspend fun createUserOnServer(user: User): UserEntity

    suspend fun validateUserToken(refreshToken: String): Boolean

    suspend fun reissueUserToken(refreshToken: String): TokenEntity

    suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity
}