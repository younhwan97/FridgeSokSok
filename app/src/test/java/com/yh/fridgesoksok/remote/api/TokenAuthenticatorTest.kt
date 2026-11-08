package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.common.SessionEvents
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import java.io.IOException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback

class TokenAuthenticatorTest {

    private lateinit var localUserDataSource: FakeLocalUserDataSource
    private lateinit var authApiService: FakeAuthApiService
    private lateinit var sessionEvents: SessionEvents
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        localUserDataSource = FakeLocalUserDataSource()
        authApiService = FakeAuthApiService()
        sessionEvents = SessionEvents()
        authenticator = TokenAuthenticator(localUserDataSource, authApiService, sessionEvents)
    }

    @Test
    fun `401 응답 시 토큰을 재발급하고 새 토큰으로 재시도해야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "refresh")
        authApiService.token = TokenResponse(accessToken = "new-access", refreshToken = "new-refresh")

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertEquals("Bearer new-access", retry?.header("Authorization"))
        assertEquals("Bearer refresh", authApiService.lastRefreshHeader)
        assertEquals("new-access", localUserDataSource.user.accessToken)
        assertEquals("new-refresh", localUserDataSource.user.refreshToken)
    }

    @Test
    fun `다른 요청이 이미 갱신한 경우 재발급 없이 저장된 토큰으로 재시도해야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "already-new", refreshToken = "refresh")

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertEquals("Bearer already-new", retry?.header("Authorization"))
        assertEquals(0, authApiService.callCount)
    }

    @Test
    fun `재발급에 실패하면 null을 반환해야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "refresh")
        authApiService.token = null

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertNull(retry)
        assertEquals("old-access", localUserDataSource.user.accessToken)
    }

    @Test
    fun `인증 API의 401 응답은 재시도하지 않아야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "refresh")

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("auth/refresh", "refresh")))

        // Then
        assertNull(retry)
        assertEquals(0, authApiService.callCount)
    }

    @Test
    fun `이미 재시도한 요청은 다시 재시도하지 않아야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "refresh")
        val first = unauthorized(request("foods/fridge/1", "old-access"))
        val second = unauthorized(request("foods/fridge/1", "old-access"), priorResponse = first)

        // When
        val retry = authenticator.authenticate(null, second)

        // Then
        assertNull(retry)
    }

    @Test
    fun `리프레시 토큰이 없으면 null을 반환해야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "")

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertNull(retry)
    }

    @Test
    fun `리프레시 토큰이 거부되면 토큰을 지우고 세션 만료 이벤트를 발행해야 한다`() = runBlocking {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "expired-refresh")
        authApiService.errorCode = 400
        val expired = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeoutOrNull(1000) { sessionEvents.expired.first() }
        }

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertNull(retry)
        assertEquals("", localUserDataSource.user.accessToken)
        assertEquals("", localUserDataSource.user.refreshToken)
        assertNotNull(expired.await())
    }

    @Test
    fun `재발급 요청 중 다른 경로에서 토큰이 회전되었다면 세션을 종료하지 않아야 한다`() {
        // Given: 재발급 요청 도중 온보딩이 새 토큰을 저장한 상황
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "old-refresh")
        authApiService.errorCode = 400
        authApiService.onCall = {
            localUserDataSource.user = userWith(accessToken = "rotated-access", refreshToken = "rotated-refresh")
        }

        // When
        val retry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertEquals("Bearer rotated-access", retry?.header("Authorization"))
        assertEquals("rotated-refresh", localUserDataSource.user.refreshToken)
    }

    @Test
    fun `네트워크 오류나 서버 오류는 세션을 종료하지 않아야 한다`() {
        // Given
        localUserDataSource.user = userWith(accessToken = "old-access", refreshToken = "refresh")

        // When: 네트워크 오류
        authApiService.networkError = IOException("timeout")
        val networkRetry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // When: 서버 오류
        authApiService.networkError = null
        authApiService.errorCode = 500
        val serverRetry = authenticator.authenticate(null, unauthorized(request("foods/fridge/1", "old-access")))

        // Then
        assertNull(networkRetry)
        assertNull(serverRetry)
        assertEquals("refresh", localUserDataSource.user.refreshToken)
    }

    private fun request(path: String, token: String): Request =
        Request.Builder()
            .url("https://soksok.io/api/$path")
            .header("Authorization", "Bearer $token")
            .build()

    private fun unauthorized(request: Request, priorResponse: Response? = null): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .priorResponse(priorResponse)
            .build()

    private fun userWith(accessToken: String, refreshToken: String) = UserEntity(
        id = "user-1",
        accessToken = accessToken,
        refreshToken = refreshToken,
        username = "tester",
        accountType = "KAKAO",
        defaultFridgeId = "fridge-1"
    )

    private class FakeAuthApiService : AuthApiService {
        var token: TokenResponse? = null
        var errorCode: Int? = null
        var networkError: IOException? = null
        var onCall: () -> Unit = {}
        var lastRefreshHeader: String? = null
        var callCount = 0

        override fun reissueToken(refreshToken: String): Call<CommonResponse<TokenResponse>> {
            callCount++
            lastRefreshHeader = refreshToken
            onCall()
            networkError?.let { error -> return FakeCall { throw error } }
            errorCode?.let { code ->
                return FakeCall { retrofit2.Response.error(code, "{}".toResponseBody("application/json".toMediaType())) }
            }
            return FakeCall { retrofit2.Response.success(CommonResponse(message = "", data = token, status = 200)) }
        }
    }

    private class FakeCall<T>(private val result: () -> retrofit2.Response<T>) : Call<T> {
        override fun execute(): retrofit2.Response<T> = result()
        override fun enqueue(callback: Callback<T>) = callback.onResponse(this, execute())
        override fun isExecuted(): Boolean = false
        override fun cancel() = Unit
        override fun isCanceled(): Boolean = false
        override fun clone(): Call<T> = FakeCall(result)
        override fun request(): Request = Request.Builder().url("https://soksok.io/api/auth/refresh").build()
        override fun timeout(): Timeout = Timeout.NONE
    }
}
