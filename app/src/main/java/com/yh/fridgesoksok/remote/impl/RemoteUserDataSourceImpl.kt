package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.KakaoApiService
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : RemoteUserDataSource {

    override suspend fun login(): String =
        kakaoApiService.createKakaoToken()
}