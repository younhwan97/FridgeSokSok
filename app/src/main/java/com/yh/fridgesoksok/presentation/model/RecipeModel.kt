package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe

data class RecipeModel(
    val id: String,
    val recipeImageUrl: String,
    val recipeName: String,
    val recipeContent: String,
    val ingredients: List<Food>,
    val createdAt: String
)

fun Recipe.toPresentation(): RecipeModel {
    return RecipeModel(
        id = id,
        recipeImageUrl = recipeImageUrl,
        recipeName = recipeName,
        recipeContent = recipeContent,
        ingredients = ingredients,
        createdAt = createdAt
    )
}