package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserDefaultFridgeUseCaseTest {

    private lateinit var getUserDefaultFridge: GetUserDefaultFridgeUseCase
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getUserDefaultFridge = GetUserDefaultFridgeUseCase(fakeUserRepository)
    }

    @Test
    fun `사용자의 기본 냉장고를 가져와야 한다`() = runBlocking {
        val result = getUserDefaultFridge("access-token").first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertNotNull(result.data)
        assertEquals("fridge-1", result.data?.id)
        assertEquals("My Fridge", result.data?.fridgeName)
    }
}
