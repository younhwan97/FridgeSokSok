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

class DeleteUserUseCaseTest {

    private lateinit var deleteUser: DeleteUserUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        deleteUser = DeleteUserUseCase(fakeUserRepository)

        // 사용자 저장
        runBlocking {
            fakeUserRepository.saveUser(
                User(
                    id = "test-id",
                    accessToken = "token",
                    refreshToken = "refresh",
                    username = "Test",
                    accountType = "KAKAO",
                    defaultFridgeId = "fridge-1"
                )
            )
        }
    }

    @Test
    fun `사용자가 삭제되어야 한다`() = runBlocking {
        val result = deleteUser().first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(true, result.data)

        // 삭제 후 사용자 정보 확인
        val loadedUser = fakeUserRepository.loadUser()
        assertEquals("", loadedUser.id)
    }
}
