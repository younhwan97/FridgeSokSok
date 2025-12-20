package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateUserOnServerUseCaseTest {

    private lateinit var createUserOnServer: CreateUserOnServerUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        createUserOnServer = CreateUserOnServerUseCase(fakeUserRepository)
    }

    @Test
    fun `서버에 사용자가 생성되어야 한다`() = runBlocking {
        val user = User(
            id = "test-id",
            accessToken = "test-access",
            refreshToken = "test-refresh",
            username = "Test User",
            accountType = "KAKAO",
            defaultFridgeId = "fridge-1"
        )

        val result = createUserOnServer(user).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals("test-id", result.data?.id)
        assertEquals("Test User", result.data?.username)
    }
}
