package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateUserTokenUseCaseTest {

    private lateinit var validateUserToken: ValidateUserTokenUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        validateUserToken = ValidateUserTokenUseCase(fakeUserRepository)
    }

    @Test
    fun `유효한 토큰이 전달되면 true를 반환해야 한다`() = runBlocking {
        val result = validateUserToken("valid-refresh-token").first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(true, result.data)
    }

    @Test
    fun `빈 토큰이 전달되면 false를 반환해야 한다`() = runBlocking {
        val result = validateUserToken("").first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(false, result.data)
    }
}
