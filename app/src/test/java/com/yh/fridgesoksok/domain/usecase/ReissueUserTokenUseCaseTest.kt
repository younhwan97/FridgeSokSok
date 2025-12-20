package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReissueUserTokenUseCaseTest {

    private lateinit var reissueUserToken: ReissueUserTokenUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        reissueUserToken = ReissueUserTokenUseCase(fakeUserRepository)
    }

    @Test
    fun `리프레시 토큰으로 새 토큰을 발급받아야 한다`() = runBlocking {
        val result = reissueUserToken("refresh-token").first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertNotNull(result.data)
        assertEquals("new-access-token", result.data?.accessToken)
        assertEquals("new-refresh-token", result.data?.refreshToken)
    }
}
