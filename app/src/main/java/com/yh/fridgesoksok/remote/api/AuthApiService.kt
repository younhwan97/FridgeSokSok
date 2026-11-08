package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 토큰 재발급 전용 API
 * TokenAuthenticator 내부에서 동기 호출되므로 Authenticator가 없는 별도 OkHttpClient를 사용한다.
 */
interface AuthApiService {
    @POST("auth/refresh")
    fun reissueToken(
        @Header("Authorization") refreshToken: String
    ): Call<CommonResponse<TokenResponse>>
}
