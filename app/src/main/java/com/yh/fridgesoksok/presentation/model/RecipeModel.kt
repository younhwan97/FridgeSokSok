package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.Recipe

data class RecipeModel(
    val id: String,
    val recipeName: String,
    val recipeContent: String,
    val createdAt: String
)

fun Recipe.toPresentation(): RecipeModel {
    return RecipeModel(
        id = id,
        recipeName = recipeName,
        recipeContent = recipeContent,
        createdAt = createdAt
    )
}