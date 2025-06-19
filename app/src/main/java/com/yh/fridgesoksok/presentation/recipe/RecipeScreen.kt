package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeListSection
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeSearchSection

@Composable
fun RecipeScreen(
    navController: NavController,
    homeNavController: NavController,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val recipes by viewModel.recipes.collectAsState()
    val typingQuery by viewModel.typingQuery.collectAsState()
    val filterQuery by viewModel.filterQuery.collectAsState()

    // 데이터 필터링
    val filteredRecipes by remember(recipes, filterQuery) {
        derivedStateOf {
            recipes.filter {
                it.recipeName.contains(filterQuery, ignoreCase = true)
                        || it.recipeContent.contains(filterQuery, ignoreCase = true)
            }
        }
    }

    // 화면
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RecipeSearchSection(
            value = typingQuery,
            onValueChange = { viewModel.updateTypingQuery(it) },
            onSearchConfirmed = { viewModel.updateFilterQuery(typingQuery) }
        )

        RecipeListSection(
            recipeState = state,
            recipes = filteredRecipes,
            onClickItem = { recipe ->
                navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                navController.navigate("recipeDetail")
            },
            onDeleteItem = { recipe ->
                viewModel.deleteRecipe(recipe.id)
            }
        )
    }
}