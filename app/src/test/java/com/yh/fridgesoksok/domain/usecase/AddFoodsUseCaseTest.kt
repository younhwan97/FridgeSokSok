package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AddFoodsUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var addFoodsUseCase: AddFoodsUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        addFoodsUseCase = AddFoodsUseCase(fakeFoodRepository)
    }

    @Test
    fun `fridgeId에 음식 목록을 추가할 수 있어야 한다`() = runBlocking {
        // Given
        val fridgeId = "test-fridge-1"
        val foods = listOf(
            Food(
                id = "food-1",
                fridgeId = fridgeId,
                itemName = "사과",
                expiryDate = "2025-12-31",
                categoryId = 1,
                count = 3,
                createdAt = "2025-01-01"
            ),
            Food(
                id = "food-2",
                fridgeId = fridgeId,
                itemName = "우유",
                expiryDate = "2025-02-01",
                categoryId = 2,
                count = 1,
                createdAt = "2025-01-01"
            )
        )

        // When
        val result = addFoodsUseCase(fridgeId, foods).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size ?: 0)
    }
}
