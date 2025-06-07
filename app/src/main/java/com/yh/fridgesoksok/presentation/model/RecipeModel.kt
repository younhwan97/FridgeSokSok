package com.yh.fridgesoksok.presentation.model

import android.os.Parcelable
import com.yh.fridgesoksok.domain.model.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeModel(
    val id: String,
    val recipeImageUrl: String,
    val recipeName: String,
    val recipeContent: String,
    val ingredients: List<String>,
    val createdAt: String
) : Parcelable

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