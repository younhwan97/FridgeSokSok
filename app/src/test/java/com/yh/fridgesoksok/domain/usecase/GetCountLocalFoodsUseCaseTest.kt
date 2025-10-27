package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.LocalFood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetCountLocalFoodsUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var getCountLocalFoodsUseCase: GetCountLocalFoodsUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        getCountLocalFoodsUseCase = GetCountLocalFoodsUseCase(fakeFoodRepository)
    }

    @Test
    fun `로컬 음식의 개수를 가져올 수 있어야 한다`() = runBlocking {
        // Given
        val localFoods = listOf(
            LocalFood(itemName = "사과", categoryId = 1, hours = 720),
            LocalFood(itemName = "우유", categoryId = 2, hours = 168),
            LocalFood(itemName = "계란", categoryId = 3, hours = 336)
        )
        fakeFoodRepository.initializeLocalFoods(localFoods).first()

        // When
        val result = getCountLocalFoodsUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(3, (result as Resource.Success).data ?: 0)
    }
}
