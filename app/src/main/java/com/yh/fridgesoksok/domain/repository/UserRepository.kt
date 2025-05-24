package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Fridge
import com.yh.fridgesoksok.domain.model.Token
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun loadUser(): User

    fun saveUser(user: User)

    fun updateUser(user: User)

    fun clearUser()

    fun createUserOnChannel(channel: Channel): Flow<Resource<User>>

    fun createUserOnServer(user: User): Flow<Resource<User>>

    fun validateUserToken(refreshToken: String): Flow<Resource<Boolean>>

    fun reissueUserToken(refreshToken: String): Flow<Resource<Token>>

    fun getUserDefaultFridge(accessToken: String): Flow<Resource<Fridge>>
}