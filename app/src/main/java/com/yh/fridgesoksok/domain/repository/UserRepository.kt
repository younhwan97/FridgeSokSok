package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserToken(): String?

    fun login(loginMethod: Int): Flow<Resource<String>>
}