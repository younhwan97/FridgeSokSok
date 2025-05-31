package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeListSection
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeSearchSection

@Composable
fun RecipeScreen(
    navController: NavController,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var input by remember { mutableStateOf("") }

    // 입력창이 비면 자동으로 검색 조건도 초기화
    LaunchedEffect(input) {
        if (input.isBlank()) {
            viewModel.updateSearchQuery("")
        }
    }

    // Content
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RecipeSearchSection(
            value = input,
            onValueChange = { input = it },
            onSearchConfirmed = {
                viewModel.updateSearchQuery(input) // searchQuery = input
            }
        )

        RecipeListSection(
            recipes = recipes.filter {
                it.recipeName.contains(searchQuery, ignoreCase = true) ||
                        it.recipeContent.contains(searchQuery, ignoreCase = true)
            },
            onClickItem = { recipe ->
                navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                navController.navigate("recipeDetail")
            }
        )
    }
}

