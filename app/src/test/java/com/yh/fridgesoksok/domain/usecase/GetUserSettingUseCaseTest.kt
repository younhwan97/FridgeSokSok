package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetUserSettingUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getUserSettingUseCase: GetUserSettingUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getUserSettingUseCase = GetUserSettingUseCase(fakeUserRepository)
    }

    @Test
    fun `사용자 설정을 가져올 수 있어야 한다`() = runBlocking {
        // When
        val result = getUserSettingUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        val setting = (result as Resource.Success).data
        assertNotNull(setting)
        assertEquals(true, setting?.receiveNotification)
        assertEquals(false, setting?.useAllIngredients)
        assertEquals(false, setting?.autoDeleteExpiredFoods)
    }
}
