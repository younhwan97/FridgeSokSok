package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(
    val userRepository: UserRepository
) {
    operator fun invoke() =
        userRepository.clearUser()
}