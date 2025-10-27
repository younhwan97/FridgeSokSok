package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.ApiUsage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetApiUsageUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getApiUsageUseCase: GetApiUsageUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getApiUsageUseCase = GetApiUsageUseCase(fakeUserRepository)
    }

    @Test
    fun `API 사용량을 가져올 수 있어야 한다`() = runBlocking {
        // Given
        val apiUsages = listOf(
            ApiUsage(feature = "recipe", max = 100, used = 25),
            ApiUsage(feature = "receipt", max = 50, used = 10)
        )
        fakeUserRepository.updateApiUsage(apiUsages).first()

        // When
        val result = getApiUsageUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size ?: 0)
    }
}
