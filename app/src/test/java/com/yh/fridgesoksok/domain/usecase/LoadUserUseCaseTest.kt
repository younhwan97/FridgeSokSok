package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoadUserUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var loadUserUseCase: LoadUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        loadUserUseCase = LoadUserUseCase(fakeUserRepository)
    }

    @Test
    fun `저장된 사용자 정보를 불러올 수 있어야 한다`() = runBlocking {
        // Given
        val user = User(
            id = "user-1",
            accessToken = "access-token",
            refreshToken = "refresh-token",
            username = "테스트 사용자",
            accountType = "NAVER",
            defaultFridgeId = "fridge-1"
        )
        fakeUserRepository.saveUser(user).first()

        // When
        val loadedUser = loadUserUseCase()

        // Then
        assertEquals("user-1", loadedUser.id)
        assertEquals("테스트 사용자", loadedUser.username)
        assertEquals("NAVER", loadedUser.accountType)
    }
}
