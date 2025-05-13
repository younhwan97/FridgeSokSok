package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class GetUserDefaultFridgeUseCase @Inject constructor(
    val repository: UserRepository
) {
    operator fun invoke() =
        repository.getUserDefaultFridge()
}