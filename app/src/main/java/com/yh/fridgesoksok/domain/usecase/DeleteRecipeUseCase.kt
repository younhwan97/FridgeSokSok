package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: String) =
        repository.deleteRecipe(recipeId)
}