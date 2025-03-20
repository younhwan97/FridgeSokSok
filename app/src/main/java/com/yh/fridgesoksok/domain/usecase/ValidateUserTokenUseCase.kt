package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class ValidateUserTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(refreshToken: String) =
        userRepository.validateUserToken(refreshToken = refreshToken)
}