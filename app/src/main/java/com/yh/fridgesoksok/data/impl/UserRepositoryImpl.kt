package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    override fun getUserToken() =
        localUserDataSource.getUserToken()

    override fun setUserToken(token: String) =
        localUserDataSource.setUserToken(token = token)


    override fun createUserToken(channel: Channel): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val user = remoteUserDataSource.createUserToken(channel).toDomain()
            emit(Resource.Success(user))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }
}