package com.yh.fridgesoksok.domain.model

data class Recipe(
    val id: String,
    val recipeImageUrl: String,
    val recipeName: String,
    val recipeContent: String,
    val ingredients: List<String>,
    val ingredientTypes: List<Int>,
    val createdAt: String
)