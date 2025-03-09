package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.LoginMethod
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.KakaoApiService
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : RemoteUserDataSource {

    override suspend fun login(loginMethod: LoginMethod): String {
        var token = ""

        when (loginMethod) {
            LoginMethod.KAKAO -> token = kakaoApiService.createKakaoToken()
            LoginMethod.NAVER -> TODO()
            LoginMethod.GOOGLE -> TODO()
            LoginMethod.FACEBOOK -> TODO()
        }

        return token
    }
}