package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(user: User): Flow<Resource<Boolean>> =
        repository.updateUser(user)
}