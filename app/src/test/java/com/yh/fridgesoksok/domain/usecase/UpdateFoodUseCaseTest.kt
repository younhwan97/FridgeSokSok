package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateFoodUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var updateFoodUseCase: UpdateFoodUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        updateFoodUseCase = UpdateFoodUseCase(fakeFoodRepository)
    }

    @Test
    fun `음식 정보를 업데이트할 수 있어야 한다`() = runBlocking {
        // Given
        val fridgeId = "test-fridge-1"
        val originalFood = Food(
            id = "food-1",
            fridgeId = fridgeId,
            itemName = "사과",
            expiryDate = "2025-12-31",
            categoryId = 1,
            count = 3,
            createdAt = "2025-01-01"
        )
        fakeFoodRepository.addFoods(fridgeId, listOf(originalFood)).first()

        val updatedFood = originalFood.copy(count = 5, itemName = "신선한 사과")

        // When
        val result = updateFoodUseCase(updatedFood).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals("신선한 사과", (result as Resource.Success).data?.itemName ?: "")
        assertEquals(5, result.data?.count ?: 0)
    }
}
