package com.yh.fridgesoksok.data.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe
import com.yh.fridgesoksok.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FakeRecipeRepository : RecipeRepository {

    private val recipes = mutableListOf<Recipe>()

    // 자동 레시피 생성 시 사용할 가상의 냉장고 재료
    private val fridgeFoods = listOf("감자", "양파", "당근", "돼지고기")

    override fun createRecipe(foods: Collection<Food>): Flow<Resource<Recipe>> {
        val recipe = Recipe(
            id = UUID.randomUUID().toString(),
            recipeImageUrl = "https://example.com/recipe.jpg",
            recipeName = "테스트 레시피",
            recipeContent = "테스트 레시피 내용",
            ingredients = foods.map { it.itemName },
            ingredientTypes = foods.map { it.categoryId },
            createdAt = "2025-01-01"
        )
        recipes.add(recipe)
        return flow { emit(Resource.Success(recipe)) }
    }

    override fun createRecipeFromFridge(): Flow<Resource<Recipe>> {
        val recipe = Recipe(
            id = UUID.randomUUID().toString(),
            recipeImageUrl = "https://example.com/auto-recipe.jpg",
            recipeName = "자동 생성 레시피",
            recipeContent = "냉장고 재료로 자동 생성된 레시피입니다.",
            ingredients = fridgeFoods,
            ingredientTypes = listOf(1, 1, 1, 2),
            createdAt = "2025-01-01"
        )
        recipes.add(recipe)
        return flow { emit(Resource.Success(recipe)) }
    }

    override fun getRecipes(): Flow<Resource<List<Recipe>>> {
        return flow { emit(Resource.Success(recipes.toList())) }
    }

    override fun deleteRecipe(recipeId: String): Flow<Resource<Boolean>> {
        val removed = recipes.removeIf { it.id == recipeId }
        return flow { emit(Resource.Success(removed)) }
    }
}
