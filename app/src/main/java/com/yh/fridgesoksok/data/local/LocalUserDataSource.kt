package com.yh.fridgesoksok.data.local

import com.yh.fridgesoksok.data.model.UserEntity

interface LocalUserDataSource {

    fun loadUser(): UserEntity

    suspend fun saveUser(userEntity: UserEntity): Boolean

    suspend fun updateUser(userEntity: UserEntity): Boolean

    suspend fun clearUser(): Boolean

    suspend fun updateUserFcmToken(fcmToken: String): Boolean

    fun getUserFcmToken(): String
}