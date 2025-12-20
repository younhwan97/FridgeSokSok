package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetLocalUserFcmTokenUseCaseTest {

    private lateinit var getLocalUserFcmToken: GetLocalUserFcmTokenUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getLocalUserFcmToken = GetLocalUserFcmTokenUseCase(fakeUserRepository)
    }

    @Test
    fun `로컬에 저장된 FCM 토큰을 가져와야 한다`() = runBlocking {
        // 먼저 토큰 저장
        runBlocking {
            fakeUserRepository.updateLocalUserFcmToken("test-token")
        }

        val token = getLocalUserFcmToken()

        assertEquals("test-token", token)
    }

    @Test
    fun `저장된 토큰이 없으면 빈 문자열을 반환해야 한다`() {
        val token = getLocalUserFcmToken()

        assertEquals("", token)
    }
}
