package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val typingQuery by viewModel.typingQuery.collectAsState()
    val filteredRecipes by viewModel.filteredRecipes.collectAsState(initial = emptyList())
    var selectedRecipeForDelete by remember { mutableStateOf<RecipeModel?>(null) }
    val deletedRecipeIds = remember { mutableStateOf(setOf<String>()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 데이터 리로드
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.getRecipes()
        }
    }

    // 서버처리 실패 시 스낵바 메시징
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // 화면
    Box(modifier = Modifier.fillMaxSize()) {
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
                onClickItem = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("recipe", it)
                    navController.navigate("recipeDetail")
                },
                onDeleteClick = {
                    selectedRecipeForDelete = it
                },
                isBeingDeleted = {
                    deletedRecipeIds.value.contains(it.id)
                },
                onDeleteAnimationEnd = {
                    deletedRecipeIds.value -= it.id
                    viewModel.removeRecipeFromList(it.id)
                },
                onRefreshClick = {
                    viewModel.getRecipes()
                }
            )
        }

        // 삭제 확인 다이얼로그
        selectedRecipeForDelete?.let { recipe ->
            ConfirmDialog(
                message = "정말 이 레시피를 삭제하시겠습니까?",
                confirmText = "삭제",
                confirmTextColor = CustomErrorColor,
                confirmContainerColor = MaterialTheme.colorScheme.errorContainer,
                onConfirm = {
                    viewModel.deleteRecipe(recipe.id) { success ->
                        if (success) {
                            deletedRecipeIds.value += recipe.id
                        }
                    }
                    selectedRecipeForDelete = null
                },
                onDismiss = {
                    selectedRecipeForDelete = null
                }
            )
        }

        // 스낵바
        Snackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 16.dp),
            snackBarHostState = snackbarHostState
        )
    }
}