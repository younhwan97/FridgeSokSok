package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Fridge
import com.yh.fridgesoksok.domain.model.Token
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.model.UserSetting
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun loadUser(): User

    fun saveUser(user: User): Flow<Resource<Boolean>>

    fun updateUser(user: User): Flow<Resource<Boolean>>

    fun clearUser(): Flow<Resource<Boolean>>

    fun createUserOnChannel(channel: Channel): Flow<Resource<User>>

    fun createUserOnServer(user: User): Flow<Resource<User>>

    fun validateUserToken(refreshToken: String): Flow<Resource<Boolean>>

    fun reissueUserToken(refreshToken: String): Flow<Resource<Token>>

    fun getUserDefaultFridge(accessToken: String): Flow<Resource<Fridge>>

    fun getUserSetting(): Flow<Resource<UserSetting>>

    fun updateReceiveNotification(enabled: Boolean): Flow<Resource<Boolean>>

    fun updateAutoDeleteExpired(enabled: Boolean): Flow<Resource<Boolean>>

    fun updateUseAllIngredients(enabled: Boolean): Flow<Resource<Boolean>>

    fun updateUserFcmToken(fcmToken: String): Flow<Resource<String>>

    fun updateLocalUserFcmToken(fcmToken: String): Flow<Resource<Boolean>>

    fun getLocalUserFcmToken(): String

    fun sendMessage(message: String): Flow<Resource<String>>
}