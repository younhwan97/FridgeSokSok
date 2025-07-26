package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class UpdateApiUsageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(apiUsages: List<ApiUsage>) =
        userRepository.updateApiUsage(apiUsages)
}