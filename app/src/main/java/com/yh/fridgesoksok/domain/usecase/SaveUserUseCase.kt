package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

// 로컬에 유저정보를 저장하는 유즈케이스
class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(user: User) =
        userRepository.saveUser(user = user)
}