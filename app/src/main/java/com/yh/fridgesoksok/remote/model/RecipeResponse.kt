package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.RecipeEntity

data class RecipeResponse(
    val id: String?,
    val recipeImageUrl: String?,
    val recipeName: String?,
    val recipeContent: String?,
    val ingredients: List<String>?,
    val createdAt: String?
)

fun RecipeResponse.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id.orEmpty(),
        recipeImageUrl = recipeImageUrl.orEmpty(),
        recipeName = recipeName.orEmpty(),
        recipeContent = recipeContent.orEmpty(),
        ingredients = ingredients.orEmpty(),
        createdAt = createdAt.orEmpty()
    )
}