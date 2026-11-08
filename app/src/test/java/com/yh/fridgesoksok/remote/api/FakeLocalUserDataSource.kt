package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.ApiUsageEntity
import com.yh.fridgesoksok.data.model.UserEntity

class FakeLocalUserDataSource : LocalUserDataSource {
    var user = UserEntity("", "", "", "", "", "")

    override fun loadUser(): UserEntity = user
    override suspend fun saveUser(userEntity: UserEntity): Boolean = true.also { user = userEntity }
    override suspend fun updateUser(userEntity: UserEntity): Boolean = true.also { user = userEntity }
    override suspend fun clearUser(): Boolean = true
    override fun updateTokens(accessToken: String, refreshToken: String): Boolean {
        user = user.copy(accessToken = accessToken, refreshToken = refreshToken)
        return true
    }
    override suspend fun updateUserFcmToken(fcmToken: String): Boolean = true
    override fun getUserFcmToken(): String = ""
    override suspend fun updateApiUsage(apiUsages: List<ApiUsageEntity>): Boolean = true
    override fun getApiUsage(feature: String): ApiUsageEntity = ApiUsageEntity(feature, 0, 0)
}
