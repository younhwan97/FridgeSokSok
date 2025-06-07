package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.repository.RecipeRepository
import javax.inject.Inject

class CreateRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    operator fun invoke(foods: Collection<Food>) =
        recipeRepository.createRecipe(foods)
}