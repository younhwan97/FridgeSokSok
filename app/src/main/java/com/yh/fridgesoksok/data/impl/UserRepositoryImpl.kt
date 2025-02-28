package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
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

    override fun login(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            //val foods = remoteUserDataSource.()
            //val domainFoods = foods.map { it.toDomain() }
            //emit(Resource.Success(domainFoods))
        } catch (exception: Exception) {
            // emit(Resource.Error(exception ?: "error"))
        }
    }
}