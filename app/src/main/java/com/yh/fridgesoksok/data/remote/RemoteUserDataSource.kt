package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity

interface RemoteUserDataSource {

    suspend fun createUserOnChannel(channel: Channel): UserEntity

    suspend fun createUserOnServer(user: UserEntity): UserEntity

    suspend fun validateUserToken(refreshToken: String): Boolean

    suspend fun reissueUserToken(refreshToken: String): TokenEntity

    suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity

}