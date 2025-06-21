package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository
) {

    operator fun invoke(fcmToken: String) =
        repository.updateUserFcmToken(fcmToken)
}