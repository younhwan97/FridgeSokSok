package com.yh.fridgesoksok.domain.repository

import com.yh.fridgesoksok.common.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserToken(): Flow<Resource<String>>
}