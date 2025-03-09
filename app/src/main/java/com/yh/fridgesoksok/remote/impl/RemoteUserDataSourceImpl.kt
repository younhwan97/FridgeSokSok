package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.KakaoApiService
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : RemoteUserDataSource {

    override suspend fun createUserToken(channel: Channel): String {
        var token = ""

        when (channel) {
            Channel.KAKAO -> token = kakaoApiService.createKakaoToken()
            Channel.NAVER -> TODO()
            Channel.GOOGLE -> TODO()
            Channel.FACEBOOK -> TODO()
        }

        return token
    }
}