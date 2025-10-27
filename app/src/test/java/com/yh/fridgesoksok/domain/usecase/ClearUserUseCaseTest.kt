package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ClearUserUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var clearUserUseCase: ClearUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        clearUserUseCase = ClearUserUseCase(fakeUserRepository)
    }

    @Test
    fun `저장된 사용자 정보를 클리어할 수 있어야 한다`() = runBlocking {
        // Given
        val user = User(
            id = "user-1",
            accessToken = "access-token",
            refreshToken = "refresh-token",
            username = "테스트 사용자",
            accountType = "GUEST",
            defaultFridgeId = "fridge-1"
        )
        fakeUserRepository.saveUser(user).first()

        // When
        val result = clearUserUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        // 클리어 후 확인
        val loadedUser = fakeUserRepository.loadUser()
        assertEquals("", loadedUser.id)
        assertEquals("", loadedUser.username)
    }
}
