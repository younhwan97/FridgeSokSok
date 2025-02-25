package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class GetUserTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke() =
        userRepository.getUserToken()
}