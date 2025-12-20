package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetServerStatusUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getServerStatusUseCase: GetServerStatusUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getServerStatusUseCase = GetServerStatusUseCase(fakeUserRepository)
    }

    @Test
    fun `서버 상태를 가져올 수 있어야 한다`() = runBlocking {
        // When
        val result = getServerStatusUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        val status = (result as Resource.Success).data
        assertNotNull(status)
        assertEquals("1.0.0", status?.version)
        assertEquals("UP", status?.status)
    }
}
