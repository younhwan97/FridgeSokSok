package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.UserEntity
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

    override fun loadUser(): User =
        localUserDataSource.loadUser().toDomain()

    override fun saveUser(user: User) =
        localUserDataSource.saveUser(
            userEntity = UserEntity(
                user.id,
                user.accessToken,
                user.refreshToken,
                user.username,
                user.accountType
            )
        )

    override fun createUserToken(channel: Channel): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val user = remoteUserDataSource.createUserToken(channel).toDomain()
            emit(Resource.Success(user))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }

    override fun createUser(token: String, username: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val user =
                remoteUserDataSource.createUser(token = token, username = username).toDomain()
            emit(Resource.Success(user))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }
}