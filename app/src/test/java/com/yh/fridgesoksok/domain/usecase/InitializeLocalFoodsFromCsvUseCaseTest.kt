package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.LocalFood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InitializeLocalFoodsFromCsvUseCaseTest {

    private lateinit var initializeLocalFoods: InitializeLocalFoodsFromCsvUseCase
    private lateinit var fakeFoodRepository: FakeFoodRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        initializeLocalFoods = InitializeLocalFoodsFromCsvUseCase(fakeFoodRepository)
    }

    @Test
    fun `로컬 음식 데이터가 초기화되어야 한다`() = runBlocking {
        val foods = listOf(
            LocalFood(itemName = "사과", categoryId = 1, hours = 168),
            LocalFood(itemName = "바나나", categoryId = 1, hours = 120)
        )

        val result = initializeLocalFoods(foods).first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(true, result.data)
    }
}
