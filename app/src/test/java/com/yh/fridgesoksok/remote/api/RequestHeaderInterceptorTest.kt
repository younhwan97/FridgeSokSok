package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.data.model.UserEntity
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class RequestHeaderInterceptorTest {

    private lateinit var localUserDataSource: FakeLocalUserDataSource
    private lateinit var interceptor: RequestHeaderInterceptor

    @Before
    fun setUp() {
        localUserDataSource = FakeLocalUserDataSource()
        localUserDataSource.user = UserEntity("user-1", "access", "refresh", "tester", "KAKAO", "fridge-1")
        interceptor = RequestHeaderInterceptor(localUserDataSource)
    }

    @Test
    fun `일반 API에는 액세스 토큰을 붙여야 한다`() {
        val chain = FakeChain(request("foods/fridge/1"))

        interceptor.intercept(chain)

        assertEquals("Bearer access", chain.proceeded?.header("Authorization"))
    }

    @Test
    fun `인증 API에는 리프레시 토큰을 붙여야 한다`() {
        val chain = FakeChain(request("auth/validateRefreshToken"))

        interceptor.intercept(chain)

        assertEquals("Bearer refresh", chain.proceeded?.header("Authorization"))
    }

    @Test
    fun `직접 지정한 Authorization 헤더는 유지해야 한다`() {
        val original = request("users/user/default-fridge").newBuilder()
            .header("Authorization", "Bearer explicit")
            .build()
        val chain = FakeChain(original)

        interceptor.intercept(chain)

        assertEquals("Bearer explicit", chain.proceeded?.header("Authorization"))
    }

    @Test
    fun `재발급 응답의 토큰을 즉시 저장해야 한다`() {
        val body = """{"message":"Success","data":{"accessToken":"new-access","refreshToken":"new-refresh"},"status":200}"""
        val chain = FakeChain(request("auth/refresh"), code = 200, body = body)

        interceptor.intercept(chain)

        assertEquals("new-access", localUserDataSource.user.accessToken)
        assertEquals("new-refresh", localUserDataSource.user.refreshToken)
    }

    @Test
    fun `재발급이 실패하면 기존 토큰을 유지해야 한다`() {
        val body = """{"message":"Invalid or expired refresh token","data":null,"status":400}"""
        val chain = FakeChain(request("auth/refresh"), code = 400, body = body)

        interceptor.intercept(chain)

        assertEquals("access", localUserDataSource.user.accessToken)
        assertEquals("refresh", localUserDataSource.user.refreshToken)
    }

    private fun request(path: String): Request =
        Request.Builder().url("https://soksok.io/api/$path").build()

    private class FakeChain(
        private val request: Request,
        private val code: Int = 200,
        private val body: String = "{}"
    ) : Interceptor.Chain {
        var proceeded: Request? = null

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            proceeded = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("")
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()
        }

        override fun connection(): Connection? = null
        override fun call(): Call = throw UnsupportedOperationException()
        override fun connectTimeoutMillis(): Int = 0
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun readTimeoutMillis(): Int = 0
        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun writeTimeoutMillis(): Int = 0
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
}
