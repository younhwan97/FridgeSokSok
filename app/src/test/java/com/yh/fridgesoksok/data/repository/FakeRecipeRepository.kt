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

    override fun getRecipes(): Flow<Resource<List<Recipe>>> {
        return flow { emit(Resource.Success(recipes.toList())) }
    }

    override fun deleteRecipe(recipeId: String): Flow<Resource<Boolean>> {
        val removed = recipes.removeIf { it.id == recipeId }
        return flow { emit(Resource.Success(removed)) }
    }
}
