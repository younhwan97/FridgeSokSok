package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateUserOnChannelUseCaseTest {

    private lateinit var createUserOnChannel: CreateUserOnChannelUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        createUserOnChannel = CreateUserOnChannelUseCase(fakeUserRepository)
    }

    @Test
    fun `KAKAO 채널로 사용자가 생성되어야 한다`() = runBlocking {
        val result = createUserOnChannel(Channel.KAKAO).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertNotNull(result.data)
        assertEquals("KAKAO", result.data?.accountType)
        assertTrue(result.data?.id?.isNotEmpty() == true)
        assertTrue(result.data?.accessToken?.isNotEmpty() == true)
    }

    @Test
    fun `NAVER 채널로 사용자가 생성되어야 한다`() = runBlocking {
        val result = createUserOnChannel(Channel.NAVER).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertNotNull(result.data)
        assertEquals("NAVER", result.data?.accountType)
    }
}
