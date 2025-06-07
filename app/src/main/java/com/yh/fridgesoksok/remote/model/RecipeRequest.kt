package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.FoodEntity

data class RecipeRequest(
    val ingredientNames: List<String>
)

fun Collection<FoodEntity>.toRecipeRequest() =
    RecipeRequest(
        ingredientNames = this.map { it.itemName + " " + it.count + "개" }
    )
