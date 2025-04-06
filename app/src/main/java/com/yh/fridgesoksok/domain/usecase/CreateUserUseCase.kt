package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

// 메인 API 서버에 유저 생성을 요청하는 유즈케이스
class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke(user: User) =
        userRepository.createUser(user = user)
}