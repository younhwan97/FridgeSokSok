package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFoodsUseCaseTest {

    private lateinit var getFoods: GetFoodsUseCase
    private lateinit var fakeFoodRepository: FakeFoodRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        getFoods = GetFoodsUseCase(fakeFoodRepository)

        val foodsToInsert = mutableListOf<Food>()
        foodsToInsert.add(
            Food(
                id = "",
                itemName = "test",
                count = 1,
                expiryDate = "2029-12-31",
                categoryId = 1,
                fridgeId = "fridge1",
                createdAt = "2025-07-01"
            )
        )

        runBlocking {
            fakeFoodRepository.addFoods("fridge1", foodsToInsert)
        }
    }

    @Test
    fun `fridgeId에 해당하는 음식 목록이 반환되어야 한다`() = runBlocking {
        val result = getFoods("fridge1").first()

        assertTrue(result is Resource.Success)
        result as Resource.Success
        assertEquals(1, result.data?.size ?: 0)
        assertEquals("test", result.data?.first()?.itemName ?: "")
        assertEquals("fridge1", result.data?.first()?.fridgeId ?: "")
    }
}