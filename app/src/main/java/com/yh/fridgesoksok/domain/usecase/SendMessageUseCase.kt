package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(message: String) =
        userRepository.sendMessage(message)
}