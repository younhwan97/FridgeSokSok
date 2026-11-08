package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.ApiUsageEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.Timeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback

class TokenAuthenticatorTest {

    private lateinit var localUserDataSource: FakeLocalUserDataSource
    private lateinit var authApiService: FakeAuthApiService
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        localUserDataSource = FakeLocalUserDataSource()
        authApiService = FakeAuthApiService()
        authenticator = TokenAuthenticator(localUserDataSource, authApiService)
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

    private class FakeLocalUserDataSource : LocalUserDataSource {
        var user = UserEntity("", "", "", "", "", "")

        override fun loadUser(): UserEntity = user
        override suspend fun saveUser(userEntity: UserEntity): Boolean = true.also { user = userEntity }
        override suspend fun updateUser(userEntity: UserEntity): Boolean = true.also { user = userEntity }
        override suspend fun clearUser(): Boolean = true
        override fun updateTokens(accessToken: String, refreshToken: String): Boolean {
            user = user.copy(accessToken = accessToken, refreshToken = refreshToken)
            return true
        }
        override suspend fun updateUserFcmToken(fcmToken: String): Boolean = true
        override fun getUserFcmToken(): String = ""
        override suspend fun updateApiUsage(apiUsages: List<ApiUsageEntity>): Boolean = true
        override fun getApiUsage(feature: String): ApiUsageEntity = ApiUsageEntity(feature, 0, 0)
    }

    private class FakeAuthApiService : AuthApiService {
        var token: TokenResponse? = null
        var lastRefreshHeader: String? = null
        var callCount = 0

        override fun reissueToken(refreshToken: String): Call<CommonResponse<TokenResponse>> {
            callCount++
            lastRefreshHeader = refreshToken
            return FakeCall(CommonResponse(message = "", data = token, status = 200))
        }
    }

    private class FakeCall<T>(private val body: T) : Call<T> {
        override fun execute(): retrofit2.Response<T> = retrofit2.Response.success(body)
        override fun enqueue(callback: Callback<T>) = callback.onResponse(this, execute())
        override fun isExecuted(): Boolean = false
        override fun cancel() = Unit
        override fun isCanceled(): Boolean = false
        override fun clone(): Call<T> = FakeCall(body)
        override fun request(): Request = Request.Builder().url("https://soksok.io/api/auth/refresh").build()
        override fun timeout(): Timeout = Timeout.NONE
    }
}
