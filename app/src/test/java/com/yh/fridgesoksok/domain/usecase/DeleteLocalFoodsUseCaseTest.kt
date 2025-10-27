package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.LocalFood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteLocalFoodsUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var deleteLocalFoodsUseCase: DeleteLocalFoodsUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        deleteLocalFoodsUseCase = DeleteLocalFoodsUseCase(fakeFoodRepository)
    }

    @Test
    fun `모든 로컬 음식을 삭제할 수 있어야 한다`() = runBlocking {
        // Given
        val localFoods = listOf(
            LocalFood(itemName = "사과", categoryId = 1, hours = 720),
            LocalFood(itemName = "우유", categoryId = 2, hours = 168)
        )
        fakeFoodRepository.initializeLocalFoods(localFoods).first()

        // When
        val result = deleteLocalFoodsUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data ?: 0)

        // 삭제 후 개수 확인
        val countResult = fakeFoodRepository.getCountLocalFoods().first()
        assertEquals(0, (countResult as Resource.Success).data ?: -1)
    }
}
