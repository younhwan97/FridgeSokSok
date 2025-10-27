package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CreateRecipeUseCaseTest {

    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var createRecipeUseCase: CreateRecipeUseCase

    @Before
    fun setUp() {
        fakeRecipeRepository = FakeRecipeRepository()
        createRecipeUseCase = CreateRecipeUseCase(fakeRecipeRepository)
    }

    @Test
    fun `음식 목록으로 레시피를 생성할 수 있어야 한다`() = runBlocking {
        // Given
        val foods = listOf(
            Food(
                id = "food-1",
                fridgeId = "fridge-1",
                itemName = "양파",
                expiryDate = "2025-12-31",
                categoryId = 1,
                count = 2,
                createdAt = "2025-01-01"
            ),
            Food(
                id = "food-2",
                fridgeId = "fridge-1",
                itemName = "당근",
                expiryDate = "2025-12-31",
                categoryId = 1,
                count = 3,
                createdAt = "2025-01-01"
            )
        )

        // When
        val result = createRecipeUseCase(foods).first()

        // Then
        assertTrue(result is Resource.Success)
        val recipe = (result as Resource.Success).data
        assertNotNull(recipe)
        assertEquals(2, recipe?.ingredients?.size ?: 0)
        assertTrue(recipe?.ingredients?.contains("양파") ?: false)
        assertTrue(recipe?.ingredients?.contains("당근") ?: false)
    }
}
