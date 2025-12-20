package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.ApiUsage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetLocalApiUsageUseCaseTest {

    private lateinit var getLocalApiUsage: GetLocalApiUsageUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getLocalApiUsage = GetLocalApiUsageUseCase(fakeUserRepository)
    }

    @Test
    fun `로컬에 저장된 API 사용량을 가져와야 한다`() = runBlocking {
        // API 사용량 저장
        val apiUsages = listOf(
            ApiUsage(feature = "recipe", max = 100, used = 10)
        )
        fakeUserRepository.updateApiUsage(apiUsages).first()

        // 조회
        val usage = getLocalApiUsage("recipe")

        assertEquals("recipe", usage.feature)
        assertEquals(100, usage.max)
        assertEquals(10, usage.used)
    }

    @Test
    fun `저장되지 않은 기능은 기본값을 반환해야 한다`() {
        val usage = getLocalApiUsage("unknown")

        assertEquals("unknown", usage.feature)
        assertEquals(100, usage.max)
        assertEquals(0, usage.used)
    }
}
