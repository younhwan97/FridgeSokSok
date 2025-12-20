package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateLocalUserFcmTokenUseCaseTest {

    private lateinit var updateLocalUserFcmToken: UpdateLocalUserFcmTokenUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateLocalUserFcmToken = UpdateLocalUserFcmTokenUseCase(fakeUserRepository)
    }

    @Test
    fun `로컬 FCM 토큰이 업데이트되어야 한다`() = runBlocking {
        val fcmToken = "local-fcm-token"
        val result = updateLocalUserFcmToken(fcmToken).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(true, result.data)
    }
}
