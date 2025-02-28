package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localUserDataSource: LocalUserDataSource
) : UserRepository {

    override fun getUserToken() =
        localUserDataSource.getUserToken()
}