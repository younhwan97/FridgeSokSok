package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetRecipesUseCaseTest {

    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var getRecipesUseCase: GetRecipesUseCase

    @Before
    fun setUp() {
        fakeRecipeRepository = FakeRecipeRepository()
        getRecipesUseCase = GetRecipesUseCase(fakeRecipeRepository)
    }

    @Test
    fun `저장된 레시피 목록을 가져올 수 있어야 한다`() = runBlocking {
        // Given
        val foods = listOf(
            Food(
                id = "food-1",
                fridgeId = "fridge-1",
                itemName = "토마토",
                expiryDate = "2025-12-31",
                categoryId = 1,
                count = 2,
                createdAt = "2025-01-01"
            )
        )
        fakeRecipeRepository.createRecipe(foods).first()

        // When
        val result = getRecipesUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size ?: 0)
    }
}
