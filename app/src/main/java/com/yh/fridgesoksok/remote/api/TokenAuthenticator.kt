package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

/**
 * 401 응답 시 리프레시 토큰으로 액세스 토큰을 재발급하고 요청을 재시도한다.
 */
class TokenAuthenticator @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val authApiService: AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request

        // 인증 API 자체의 실패이거나 이미 재시도한 요청이면 포기
        if (request.isAuthRequest() || response.priorResponse != null) return null

        synchronized(AuthTokenLock) {
            val user = localUserDataSource.loadUser()
            if (user.refreshToken.isBlank()) return null

            // 대기 중 다른 요청이 이미 토큰을 갱신했다면 새 토큰으로 재시도
            val requestToken = request.header(HEADER_AUTHORIZATION)?.removePrefix(BEARER_PREFIX)
            if (user.accessToken.isNotBlank() && requestToken != user.accessToken) {
                return request.withBearerToken(user.accessToken)
            }

            val token = try {
                authApiService.reissueToken("$BEARER_PREFIX${user.refreshToken}").execute().body()?.data
            } catch (e: Exception) {
                Logger.e(TAG, "토큰 재발급 실패", e)
                null
            }

            val accessToken = token?.accessToken
            val refreshToken = token?.refreshToken
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) return null

            localUserDataSource.updateTokens(accessToken, refreshToken)
            Logger.d(TAG, "토큰 재발급 성공")
            return request.withBearerToken(accessToken)
        }
    }
}
