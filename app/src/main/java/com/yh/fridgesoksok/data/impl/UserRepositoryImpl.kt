package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource
) : UserRepository {

    override fun getUserToken(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val userToken = localUserDataSource.getUserToken()
            emit(Resource.Success(userToken))
        } catch (exception: Exception) {
            // emit(Resource.Error(exception ?: "error"))
        }
    }
}