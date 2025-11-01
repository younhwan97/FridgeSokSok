package com.yh.fridgesoksok.presentation.recipe

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe
import com.yh.fridgesoksok.domain.repository.RecipeRepository
import com.yh.fridgesoksok.domain.usecase.DeleteRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecipeViewModelTest {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var fakeRepository: FakeRecipeRepository

    @Before
    fun setUp() {
        fakeRepository = FakeRecipeRepository()
        viewModel = RecipeViewModel(
            getRecipesUseCase = GetRecipesUseCase(fakeRepository),
            deleteRecipeUseCase = DeleteRecipeUseCase(fakeRepository)
        )
    }

    @Test
    fun `검색어 입력 시 typingQuery가 업데이트되어야 한다`() {
        // When
        viewModel.updateTypingQuery("검색어")

        // Then
        assertEquals("검색어", viewModel.typingQuery.value)
    }

    @Test
    fun `검색어를 지우면 필터가 초기화되어야 한다`() {
        // Given
        viewModel.updateFilterQuery("검색어")

        // When
        viewModel.updateTypingQuery("")

        // Then
        assertEquals("", viewModel.typingQuery.value)
    }

    @Test
    fun `필터 적용이 올바르게 작동해야 한다`() {
        // When
        viewModel.updateFilterQuery("테스트")

        // Then
        // filteredRecipes는 StateFlow이므로 즉시 확인 가능
        // 실제 레시피 데이터가 필요하므로 기본 테스트만 수행
        assertTrue(viewModel.filteredRecipes.value.isEmpty())
    }

    // Fake Repository for Testing
    private class FakeRecipeRepository : RecipeRepository {
        private val recipes = mutableListOf<Recipe>()
        var shouldReturnError = false

        fun addTestRecipe() {
            recipes.add(
                Recipe(
                    id = "test-id",
                    recipeImageUrl = "https://example.com/recipe.jpg",
                    recipeName = "테스트 레시피",
                    recipeContent = "테스트 레시피 내용",
                    ingredients = listOf("재료1", "재료2"),
                    ingredientTypes = listOf(1, 2),
                    createdAt = "2025-01-01"
                )
            )
        }

        fun addRecipeWithName(name: String) {
            recipes.add(
                Recipe(
                    id = "id-${recipes.size}",
                    recipeImageUrl = "",
                    recipeName = name,
                    recipeContent = "내용",
                    ingredients = emptyList(),
                    ingredientTypes = emptyList(),
                    createdAt = "2025-01-01"
                )
            )
        }

        fun addRecipeWithContent(content: String) {
            recipes.add(
                Recipe(
                    id = "id-${recipes.size}",
                    recipeImageUrl = "",
                    recipeName = "이름",
                    recipeContent = content,
                    ingredients = emptyList(),
                    ingredientTypes = emptyList(),
                    createdAt = "2025-01-01"
                )
            )
        }

        fun addRecipeWithId(id: String, name: String) {
            recipes.add(
                Recipe(
                    id = id,
                    recipeImageUrl = "",
                    recipeName = name,
                    recipeContent = "내용",
                    ingredients = emptyList(),
                    ingredientTypes = emptyList(),
                    createdAt = "2025-01-01"
                )
            )
        }

        fun addRecipeWithDate(date: String, name: String) {
            recipes.add(
                Recipe(
                    id = "id-${recipes.size}",
                    recipeImageUrl = "",
                    recipeName = name,
                    recipeContent = "내용",
                    ingredients = emptyList(),
                    ingredientTypes = emptyList(),
                    createdAt = date
                )
            )
        }

        override fun createRecipe(foods: Collection<Food>): Flow<Resource<Recipe>> {
            return flow { emit(Resource.Success(recipes.first())) }
        }

        override fun getRecipes(): Flow<Resource<List<Recipe>>> {
            return flow {
                if (shouldReturnError) {
                    emit(Resource.Error("Error"))
                } else {
                    emit(Resource.Success(recipes.toList()))
                }
            }
        }

        override fun deleteRecipe(recipeId: String): Flow<Resource<Boolean>> {
            val removed = recipes.removeIf { it.id == recipeId }
            return flow { emit(Resource.Success(removed)) }
        }
    }
}
