package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(user: User) =
        userRepository.saveUser(user = user)
}