package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity

data class RecipeRequest(
    val ingredientNames: List<String>,
    val ingredientTypes: List<Int>
)

fun Collection<FoodEntity>.toRecipeRequest() =
    RecipeRequest(
        ingredientNames = this.map { it.itemName + " " + it.count + "개" },
        ingredientTypes = this.map { it.categoryId }
    )
