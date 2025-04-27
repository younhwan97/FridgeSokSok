package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

// 채널(KAKAO, NAVER, GOOGLE.. etc)을 선택하여 토큰을 생성하는 유즈케이스
// 생성된 토큰은 서버에서 회원가입, 로그인 처리 시 사용
class CreateUserTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(channel: Channel) =
        userRepository.createUserToken(channel = channel)
}