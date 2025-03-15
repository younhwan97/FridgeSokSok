package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.model.UserResponse
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : RemoteUserDataSource {

    override suspend fun createUserToken(channel: Channel): UserEntity {

        val userResponse: UserResponse = when (channel) {
            Channel.KAKAO -> kakaoApiService.createKakaoToken()
            Channel.NAVER -> TODO()
            Channel.GOOGLE -> TODO()
            Channel.FACEBOOK -> TODO()
            else -> UserResponse(id = -1L, token = "", channel = "")
        }

        return userResponse.toData()
    }
}