package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.model.UserSettingEntity

interface RemoteUserDataSource {

    suspend fun createUserOnChannel(channel: Channel): UserEntity

    suspend fun createUserOnServer(user: UserEntity): UserEntity

    suspend fun deleteUser(): Boolean

    suspend fun validateUserToken(refreshToken: String): Boolean

    suspend fun reissueUserToken(refreshToken: String): TokenEntity

    suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity

    suspend fun updateReceiveNotification(enabled: Boolean): Boolean

    suspend fun updateAutoDeleteExpired(enabled: Boolean): Boolean

    suspend fun updateUseAllIngredients(enabled: Boolean): Boolean

    suspend fun getUserSettings(): UserSettingEntity

    suspend fun updateUserFcmToken(fcmToken: String): String

    suspend fun sendMessage(message: String): String
}