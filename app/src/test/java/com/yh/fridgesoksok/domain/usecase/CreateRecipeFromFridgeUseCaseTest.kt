package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CreateRecipeFromFridgeUseCaseTest {

    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var createRecipeFromFridgeUseCase: CreateRecipeFromFridgeUseCase

    @Before
    fun setUp() {
        fakeRecipeRepository = FakeRecipeRepository()
        createRecipeFromFridgeUseCase = CreateRecipeFromFridgeUseCase(fakeRecipeRepository)
    }

    @Test
    fun `냉장고 재료로 레시피를 자동 생성할 수 있어야 한다`() = runBlocking {
        // When
        val result = createRecipeFromFridgeUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        val recipe = (result as Resource.Success).data
        assertNotNull(recipe)
        assertEquals("자동 생성 레시피", recipe?.recipeName)
    }

    @Test
    fun `자동 생성된 레시피에 재료가 포함되어야 한다`() = runBlocking {
        // When
        val result = createRecipeFromFridgeUseCase().first()

        // Then
        assertTrue(result is Resource.Success)
        val recipe = (result as Resource.Success).data
        assertNotNull(recipe?.ingredients)
        assertTrue((recipe?.ingredients?.size ?: 0) > 0)
    }

    @Test
    fun `자동 생성된 레시피가 레시피 목록에 추가되어야 한다`() = runBlocking {
        // Given
        val initialRecipes = fakeRecipeRepository.getRecipes().first()
        val initialCount = (initialRecipes as Resource.Success).data?.size ?: 0

        // When
        createRecipeFromFridgeUseCase().first()

        // Then
        val afterRecipes = fakeRecipeRepository.getRecipes().first()
        val afterCount = (afterRecipes as Resource.Success).data?.size ?: 0
        assertEquals(initialCount + 1, afterCount)
    }
}
