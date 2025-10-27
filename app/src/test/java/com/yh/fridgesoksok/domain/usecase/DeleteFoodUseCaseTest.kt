package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteFoodUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var deleteFoodUseCase: DeleteFoodUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        deleteFoodUseCase = DeleteFoodUseCase(fakeFoodRepository)
    }

    @Test
    fun `foodId에 해당하는 음식이 삭제되어야 한다`() = runBlocking {
        // Given: 테스트 데이터 추가
        val fridgeId = "test-fridge-1"
        val testFood = Food(
            id = "food-1",
            fridgeId = fridgeId,
            itemName = "테스트 음식",
            expiryDate = "2025-12-31",
            categoryId = 1,
            count = 2,
            createdAt = "2025-01-01"
        )
        fakeFoodRepository.addFoods(fridgeId, listOf(testFood)).first()

        // When: 음식 삭제
        val result = deleteFoodUseCase(testFood.id).first()

        // Then: 삭제 성공 확인
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        // 실제로 음식이 삭제되었는지 확인
        val remainingFoods = fakeFoodRepository.getFoods(fridgeId).first()
        assertTrue(remainingFoods is Resource.Success)
        assertEquals(0, (remainingFoods as Resource.Success).data?.size ?: -1)
    }
}