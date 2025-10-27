package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateUserUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateUserUseCase = UpdateUserUseCase(fakeUserRepository)
    }

    @Test
    fun `사용자 정보를 업데이트할 수 있어야 한다`() = runBlocking {
        // Given
        val originalUser = User(
            id = "user-1",
            accessToken = "old-token",
            refreshToken = "old-refresh",
            username = "원래 이름",
            accountType = "KAKAO",
            defaultFridgeId = "fridge-1"
        )
        fakeUserRepository.saveUser(originalUser).first()

        val updatedUser = originalUser.copy(
            username = "새로운 이름",
            accessToken = "new-token"
        )

        // When
        val result = updateUserUseCase(updatedUser).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        // 업데이트된 정보 확인
        val loadedUser = fakeUserRepository.loadUser()
        assertEquals("새로운 이름", loadedUser.username)
        assertEquals("new-token", loadedUser.accessToken)
    }
}
