package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import com.yh.fridgesoksok.domain.model.Food
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteRecipeUseCaseTest {

    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase

    @Before
    fun setUp() {
        fakeRecipeRepository = FakeRecipeRepository()
        deleteRecipeUseCase = DeleteRecipeUseCase(fakeRecipeRepository)
    }

    @Test
    fun `레시피를 삭제할 수 있어야 한다`() = runBlocking {
        // Given
        val foods = listOf(
            Food(
                id = "food-1",
                fridgeId = "fridge-1",
                itemName = "감자",
                expiryDate = "2025-12-31",
                categoryId = 1,
                count = 5,
                createdAt = "2025-01-01"
            )
        )
        val createResult = fakeRecipeRepository.createRecipe(foods).first()
        val recipeId = (createResult as Resource.Success).data?.id ?: ""

        // When
        val deleteResult = deleteRecipeUseCase(recipeId).first()

        // Then
        assertTrue(deleteResult is Resource.Success)
        assertEquals(true, (deleteResult as Resource.Success).data)

        // 삭제 후 목록 확인
        val recipesResult = fakeRecipeRepository.getRecipes().first()
        assertEquals(0, (recipesResult as Resource.Success).data?.size ?: -1)
    }
}
