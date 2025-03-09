package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Error
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    override fun getUserToken() =
        localUserDataSource.getUserToken()

    override fun login(loginMethod: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val token = remoteUserDataSource.login()
            emit(Resource.Success(token))
        } catch (exception: Exception) {
            //emit(Resource.Error(exception ?: "error"))
        }
    }
}