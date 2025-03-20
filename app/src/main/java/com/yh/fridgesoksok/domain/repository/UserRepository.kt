package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun loadUser(): User

    fun saveUser(user: User)

    fun createUserToken(channel: Channel): Flow<Resource<User>>

    fun createUser(token:String, username:String): Flow<Resource<User>>

    fun validateUserToken(refreshToken: String): Flow<Resource<Boolean>>
}