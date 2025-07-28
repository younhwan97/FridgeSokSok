package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class GetApiUsageFromLocalUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(feature: String) =
        userRepository.getApiUsageFromLocal(feature)
}