package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.toDomain
import com.yh.fridgesoksok.data.model.toEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.model.Fridge
import com.yh.fridgesoksok.domain.model.Token
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    override fun loadUser() =
        localUserDataSource.loadUser().toDomain()

    override fun saveUser(user: User): Flow<Resource<Boolean>> =
        flowWithResource<Boolean> {
            localUserDataSource.saveUser(user.toEntity())
        }

    override fun updateUser(user: User): Flow<Resource<Boolean>> =
        flowWithResource<Boolean> {
            localUserDataSource.updateUser(user.toEntity())
        }

    override fun clearUser(): Flow<Resource<Boolean>> =
        flowWithResource<Boolean> {
            localUserDataSource.clearUser()
        }

    override fun createUserOnChannel(channel: Channel): Flow<Resource<User>> =
        flowWithResource {
            remoteUserDataSource.createUserOnChannel(channel).toDomain()
        }

    override fun createUserOnServer(user: User): Flow<Resource<User>> =
        flowWithResource {
            remoteUserDataSource.createUserOnServer(user = user.toEntity()).toDomain()
        }

    override fun validateUserToken(refreshToken: String): Flow<Resource<Boolean>> =
        flowWithResource {
            remoteUserDataSource.validateUserToken(refreshToken = refreshToken)
        }

    override fun reissueUserToken(refreshToken: String): Flow<Resource<Token>> =
        flowWithResource {
            remoteUserDataSource.reissueUserToken(refreshToken = refreshToken).toDomain()
        }

    override fun getUserDefaultFridge(accessToken: String): Flow<Resource<Fridge>> =
        flowWithResource {
            remoteUserDataSource.getUserDefaultFridge(accessToken).toDomain()
        }

    private inline fun <T> flowWithResource(crossinline block: suspend () -> T): Flow<Resource<T>> =
        flow {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(block()))
            } catch (exception: Exception) {
                emit(Resource.Error(exception.localizedMessage ?: "Unknown Error"))
            }
        }
}