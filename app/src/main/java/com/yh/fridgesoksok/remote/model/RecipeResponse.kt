package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.RecipeEntity

data class RecipeResponse(
    val id: String?,
    val recipeName: String?,
    val recipeContent: String?,
    val createdAt: String?
)

fun RecipeResponse.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id.orEmpty(),
        recipeName = recipeName.orEmpty(),
        recipeContent = recipeContent.orEmpty(),
        createdAt = createdAt.orEmpty()
    )
}
