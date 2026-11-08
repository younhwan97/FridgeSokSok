package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.SessionEvents
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

// 리프레시 토큰이 거부된 것으로 판단하는 응답 코드 (서버는 만료/무효 시 400 반환)
private val SESSION_EXPIRED_CODES = setOf(400, 401, 403)

/**
 * 401 응답 시 리프레시 토큰으로 액세스 토큰을 재발급하고 요청을 재시도한다.
 * 리프레시 토큰까지 거부되면 토큰을 지우고 세션 만료 이벤트를 발행한다.
 */
class TokenAuthenticator @Inject constructor(
    private val localUserDataSource: LocalUserDataSource,
    private val authApiService: AuthApiService,
    private val sessionEvents: SessionEvents
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

            val reissueResponse = try {
                authApiService.reissueToken("$BEARER_PREFIX${user.refreshToken}").execute()
            } catch (e: Exception) {
                // 네트워크 오류는 세션 만료로 보지 않음
                Logger.e(TAG, "토큰 재발급 요청 실패", e)
                return null
            }

            if (reissueResponse.code() in SESSION_EXPIRED_CODES) {
                return handleRefreshRejected(request, usedRefreshToken = user.refreshToken)
            }

            val token = reissueResponse.body()?.data
            val accessToken = token?.accessToken
            val refreshToken = token?.refreshToken
            if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) return null

            localUserDataSource.updateTokens(accessToken, refreshToken)
            Logger.d(TAG, "토큰 재발급 성공")
            return request.withBearerToken(accessToken)
        }
    }

    private fun handleRefreshRejected(request: Request, usedRefreshToken: String): Request? {
        // 요청 중 다른 경로(온보딩 재발급 등)에서 토큰이 회전되었다면 만료가 아님
        val latest = localUserDataSource.loadUser()
        if (latest.refreshToken != usedRefreshToken && latest.accessToken.isNotBlank()) {
            return request.withBearerToken(latest.accessToken)
        }

        Logger.d(TAG, "리프레시 토큰 만료 → 세션 종료")
        localUserDataSource.updateTokens(accessToken = "", refreshToken = "")
        sessionEvents.notifyExpired()
        return null
    }
}
