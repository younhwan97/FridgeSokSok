package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.ApiUsage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateApiUsageUseCaseTest {

    private lateinit var updateApiUsage: UpdateApiUsageUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateApiUsage = UpdateApiUsageUseCase(fakeUserRepository)
    }

    @Test
    fun `API 사용량이 업데이트되어야 한다`() = runBlocking {
        val apiUsages = listOf(
            ApiUsage(feature = "recipe", max = 100, used = 10),
            ApiUsage(feature = "ocr", max = 50, used = 5)
        )

        val result = updateApiUsage(apiUsages).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(true, result.data)
    }
}
