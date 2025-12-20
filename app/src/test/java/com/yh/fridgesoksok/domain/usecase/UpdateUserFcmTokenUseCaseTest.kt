package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateUserFcmTokenUseCaseTest {

    private lateinit var updateUserFcmToken: UpdateUserFcmTokenUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateUserFcmToken = UpdateUserFcmTokenUseCase(fakeUserRepository)
    }

    @Test
    fun `FCM 토큰이 업데이트되어야 한다`() = runBlocking {
        val fcmToken = "new-fcm-token"
        val result = updateUserFcmToken(fcmToken).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(fcmToken, result.data)
    }
}
