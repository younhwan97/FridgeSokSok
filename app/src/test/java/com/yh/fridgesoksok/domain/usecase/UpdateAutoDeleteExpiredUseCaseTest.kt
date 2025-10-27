package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateAutoDeleteExpiredUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var updateAutoDeleteExpiredUseCase: UpdateAutoDeleteExpiredUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateAutoDeleteExpiredUseCase = UpdateAutoDeleteExpiredUseCase(fakeUserRepository)
    }

    @Test
    fun `유통기한 지난 음식 자동 삭제 설정을 변경할 수 있어야 한다`() = runBlocking {
        // When
        val result = updateAutoDeleteExpiredUseCase(true).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        // 변경된 설정 확인
        val settingResult = fakeUserRepository.getUserSetting().first()
        assertEquals(true, (settingResult as Resource.Success).data?.autoDeleteExpiredFoods)
    }
}
