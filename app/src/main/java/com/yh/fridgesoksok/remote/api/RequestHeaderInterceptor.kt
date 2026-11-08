package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.data.local.LocalUserDataSource
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

internal const val HEADER_AUTHORIZATION = "Authorization"
internal const val BEARER_PREFIX = "Bearer "

internal fun Request.withBearerToken(token: String): Request =
    newBuilder().header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$token").build()

internal fun Request.isAuthRequest(): Boolean =
    url.encodedPath.contains("/auth/")

class RequestHeaderInterceptor @Inject constructor(
    private val localUserDataSource: LocalUserDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 호출부에서 직접 헤더를 지정한 경우 그대로 사용
        if (originalRequest.header(HEADER_AUTHORIZATION) != null) {
            return chain.proceed(originalRequest)
        }

        val user = localUserDataSource.loadUser()
        // 인증(auth/) API는 리프레시 토큰, 그 외는 액세스 토큰 사용
        val token = if (originalRequest.isAuthRequest()) user.refreshToken else user.accessToken

        val request = if (token.isNotBlank()) originalRequest.withBearerToken(token) else originalRequest
        return chain.proceed(request)
    }
}
