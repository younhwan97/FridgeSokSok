package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {

    operator fun invoke() =
        recipeRepository.getRecipes()
}