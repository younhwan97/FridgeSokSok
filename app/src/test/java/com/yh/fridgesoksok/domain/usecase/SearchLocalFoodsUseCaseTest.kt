package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.LocalFood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SearchLocalFoodsUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var searchLocalFoodsUseCase: SearchLocalFoodsUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        searchLocalFoodsUseCase = SearchLocalFoodsUseCase(fakeFoodRepository)
    }

    @Test
    fun `키워드로 로컬 음식을 검색할 수 있어야 한다`() = runBlocking {
        // Given
        val localFoods = listOf(
            LocalFood(itemName = "사과", categoryId = 1, hours = 720),
            LocalFood(itemName = "사과주스", categoryId = 2, hours = 480),
            LocalFood(itemName = "우유", categoryId = 2, hours = 168)
        )
        fakeFoodRepository.initializeLocalFoods(localFoods).first()

        // When
        val result = searchLocalFoodsUseCase("사과").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size ?: 0)
        assertTrue(result.data?.all { it.itemName.contains("사과") } ?: false)
    }
}
