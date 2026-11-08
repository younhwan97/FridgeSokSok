package com.yh.fridgesoksok.data.local

import com.yh.fridgesoksok.data.model.ApiUsageEntity
import com.yh.fridgesoksok.data.model.UserEntity

interface LocalUserDataSource {

    fun loadUser(): UserEntity

    suspend fun saveUser(userEntity: UserEntity): Boolean

    suspend fun updateUser(userEntity: UserEntity): Boolean

    suspend fun clearUser(): Boolean

    // OkHttp Authenticator(동기 컨텍스트)에서 호출되므로 suspend가 아님
    fun updateTokens(accessToken: String, refreshToken: String): Boolean

    suspend fun updateUserFcmToken(fcmToken: String): Boolean

    fun getUserFcmToken(): String

    suspend fun updateApiUsage(apiUsages: List<ApiUsageEntity>): Boolean

    fun getApiUsage(feature: String): ApiUsageEntity
}