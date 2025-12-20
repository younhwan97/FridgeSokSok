package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private lateinit var sendMessage: SendMessageUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        sendMessage = SendMessageUseCase(fakeUserRepository)
    }

    @Test
    fun `메시지가 전송되어야 한다`() = runBlocking {
        val message = "Test message"
        val result = sendMessage(message).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertTrue(result.data?.contains(message) == true)
    }
}
