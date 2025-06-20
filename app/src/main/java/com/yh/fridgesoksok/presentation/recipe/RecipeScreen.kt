package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.common.ConfirmDialog
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeListSection
import com.yh.fridgesoksok.presentation.recipe.comp.RecipeSearchSection
import com.yh.fridgesoksok.presentation.theme.CustomErrorColor

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

    // 삭제 요청된 레시피 & 애니메이션 대상 ID 목록
    var selectedRecipeForDelete by remember { mutableStateOf<RecipeModel?>(null) }
    val deletedRecipeIds = remember { mutableStateListOf<String>() }

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
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("recipe", recipe)
                navController.navigate("recipeDetail")
            },
            onDeleteClick = { recipe ->
                selectedRecipeForDelete = recipe
            },
            isBeingDeleted = { recipe ->
                deletedRecipeIds.contains(recipe.id)
            },
            onDeleteAnimationEnd = { recipe ->
                viewModel.deleteRecipe(recipe.id)
                deletedRecipeIds.remove(recipe.id)
            }
        )
    }

    // 삭제 확인 다이얼로그
    selectedRecipeForDelete?.let { recipe ->
        ConfirmDialog(
            title = "레시피 삭제",
            message = "정말 이 레시피를 삭제하시겠습니까?",
            confirmText = "삭제",
            confirmTextColor = CustomErrorColor,
            confirmContainerColor = MaterialTheme.colorScheme.errorContainer,
            onConfirm = {
                deletedRecipeIds.add(recipe.id)
                selectedRecipeForDelete = null
            },
            onDismiss = {
                selectedRecipeForDelete = null
            }
        )
    }
}