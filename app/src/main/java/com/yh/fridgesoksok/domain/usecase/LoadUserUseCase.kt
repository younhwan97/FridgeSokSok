package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

// 로컬에 저장된 유저정보를 가져오는 유즈케이스
class LoadUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() =
        userRepository.loadUser()
}