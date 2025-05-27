package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.Recipe

data class RecipeEntity(
    val id: String,
    val recipeName: String,
    val recipeContent: String,
    val createdAt: String
)

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        recipeName = recipeName,
        recipeContent = recipeContent,
        createdAt = createdAt
    )
}

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        recipeName = recipeName,
        recipeContent = recipeContent,
        createdAt = createdAt
    )
}