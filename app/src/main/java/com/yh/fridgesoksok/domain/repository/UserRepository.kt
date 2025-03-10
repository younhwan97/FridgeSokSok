package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserToken(): String?

    fun setUserToken(token: String): Unit

    fun createUserToken(channel: Channel): Flow<Resource<String>>
}