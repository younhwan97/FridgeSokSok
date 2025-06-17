package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.Recipe

data class RecipeEntity(
    val id: String,
    val recipeImageUrl: String,
    val recipeName: String,
    val recipeContent: String,
    val ingredients: List<String>,
    val ingredientTypes: List<Int>,
    val createdAt: String
)

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        recipeImageUrl = recipeImageUrl,
        recipeName = recipeName,
        recipeContent = recipeContent,
        ingredients = ingredients,
        ingredientTypes = ingredientTypes,
        createdAt = createdAt
    )
}

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        recipeImageUrl = recipeImageUrl,
        recipeName = recipeName,
        recipeContent = recipeContent,
        ingredients = ingredients,
        ingredientTypes = ingredientTypes,
        createdAt = createdAt
    )
}