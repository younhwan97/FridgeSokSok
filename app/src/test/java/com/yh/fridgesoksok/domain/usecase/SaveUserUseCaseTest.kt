package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SaveUserUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var saveUserUseCase: SaveUserUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        saveUserUseCase = SaveUserUseCase(fakeUserRepository)
    }

    @Test
    fun `사용자 정보를 저장할 수 있어야 한다`() = runBlocking {
        // Given
        val user = User(
            id = "user-1",
            accessToken = "access-token",
            refreshToken = "refresh-token",
            username = "테스트 사용자",
            accountType = "KAKAO",
            defaultFridgeId = "fridge-1"
        )

        // When
        val result = saveUserUseCase(user).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        // 저장된 사용자 확인
        val savedUser = fakeUserRepository.loadUser()
        assertEquals("user-1", savedUser.id)
        assertEquals("테스트 사용자", savedUser.username)
    }
}
