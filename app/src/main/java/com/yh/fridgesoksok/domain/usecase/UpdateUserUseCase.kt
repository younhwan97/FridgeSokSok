package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(user: User) =
        repository.updateUser(user)
}