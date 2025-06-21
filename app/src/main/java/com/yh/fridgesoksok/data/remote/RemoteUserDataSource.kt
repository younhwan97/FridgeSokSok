package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface RemoteUserDataSource {

    suspend fun createUserOnChannel(channel: Channel): UserEntity

    suspend fun createUserOnServer(user: UserEntity): UserEntity

    suspend fun validateUserToken(refreshToken: String): Boolean

    suspend fun reissueUserToken(refreshToken: String): TokenEntity

    suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity

    suspend fun updateExpirationAlarmEnabled(enabled: Boolean): Boolean

    suspend fun updateAutoDeleteExpiredFoodEnabled(enabled: Boolean): Boolean

    suspend fun updateUseAllIngredientsEnabled(enabled: Boolean): Boolean

    suspend fun updateUserFcmToken(fcmToken: String): String
}