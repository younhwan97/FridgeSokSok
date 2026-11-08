package com.yh.fridgesoksok.remote.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
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

private fun Request.isRefreshRequest(): Boolean =
    url.encodedPath.endsWith("/auth/refresh")

private const val MAX_TOKEN_BODY_BYTES = 64L * 1024

/**
 * 리프레시 토큰을 사용하는 작업(auth/ API 호출, TokenAuthenticator 재발급)을 직렬화하는 락
 * 재발급으로 리프레시 토큰이 회전되는 도중 이전 토큰으로 auth/ API를 호출하는 경합을 막는다.
 */
internal object AuthTokenLock

class RequestHeaderInterceptor @Inject constructor(
    private val localUserDataSource: LocalUserDataSource
) : Interceptor {

    private val gson = Gson()
    private val tokenResponseType = object : TypeToken<CommonResponse<TokenResponse>>() {}.type

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (!originalRequest.isAuthRequest()) {
            return chain.proceed(originalRequest.withStoredToken { it.accessToken })
        }

        // 인증(auth/) API는 리프레시 토큰 사용, 진행 중인 재발급이 끝난 뒤 최신 토큰으로 호출
        synchronized(AuthTokenLock) {
            val response = chain.proceed(originalRequest.withStoredToken { it.refreshToken })
            // 재발급으로 회전된 토큰은 락을 풀기 전에 저장해 다음 호출이 이전 토큰을 쓰지 않도록 함
            if (originalRequest.isRefreshRequest() && response.isSuccessful) {
                saveReissuedTokens(response)
            }
            return response
        }
    }

    private fun saveReissuedTokens(response: Response) {
        val token = try {
            val body = response.peekBody(MAX_TOKEN_BODY_BYTES).string()
            gson.fromJson<CommonResponse<TokenResponse>>(body, tokenResponseType)?.data
        } catch (e: Exception) {
            null
        } ?: return

        val accessToken = token.accessToken
        val refreshToken = token.refreshToken
        if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
            localUserDataSource.updateTokens(accessToken, refreshToken)
        }
    }

    // 호출부에서 직접 헤더를 지정한 경우 그대로 사용
    private fun Request.withStoredToken(selector: (UserEntity) -> String): Request {
        if (header(HEADER_AUTHORIZATION) != null) return this
        val token = selector(localUserDataSource.loadUser())
        return if (token.isNotBlank()) withBearerToken(token) else this
    }
}
