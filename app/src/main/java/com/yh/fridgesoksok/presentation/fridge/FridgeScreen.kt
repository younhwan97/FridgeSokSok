package com.yh.fridgesoksok.presentation.fridge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.RecipeGenerationState
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.fridge.comp.FoodListSection
import com.yh.fridgesoksok.presentation.fridge.comp.FoodTypeFilter
import com.yh.fridgesoksok.presentation.fridge.comp.FridgeSearchSection
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.model.Type

@Composable
fun FridgeScreen(
    mode: HomeUiMode,
    navController: NavController,
    homeNavController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: FridgeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val typingQuery by viewModel.typingQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedFoods by viewModel.selectedFoods.collectAsState()
    val filteredFoods by viewModel.filteredFoods.collectAsState()

    // 공유 뷰모델 상태값
    val selectAllRequested by sharedViewModel.selectAllFoodsRequested.collectAsState()
    val deselectAllRequested by sharedViewModel.deselectAllFoodsRequested.collectAsState()
    val recipeState by sharedViewModel.recipeGenerationState.collectAsState()

    // 화면 재진입 시 데이터 로드
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadFoods()
        }
    }

    // 전체 선택/해제 처리
    LaunchedEffect(selectAllRequested, deselectAllRequested) {
        when {
            selectAllRequested -> {
                viewModel.setSelectedFoods(filteredFoods.toSet())
                sharedViewModel.clearSelectAllFoodsRequest()
            }

            deselectAllRequested -> {
                viewModel.setDeselectedFoods(filteredFoods.toSet())
                sharedViewModel.clearDeselectAllFoodsRequest()
            }
        }
    }

    // 레시피 생성 상태 감지 및 처리
    LaunchedEffect(recipeState) {
        when (recipeState) {
            is RecipeGenerationState.Loading -> {
                if (selectedFoods.isNotEmpty()) {
                    viewModel.createRecipe(selectedFoods) { success, errorMsg ->
                        if (recipeState == RecipeGenerationState.Loading) {
                            if (success) {
                                sharedViewModel.completeRecipeGeneration(success = true)
                            } else {
                                sharedViewModel.completeRecipeGeneration(success = false, errorMessage = errorMsg)
                            }
                        }
                    }
                } else {
                    sharedViewModel.completeRecipeGeneration(success = false, errorMessage = "재료 미선택")
                }
            }

            is RecipeGenerationState.Success -> {
                homeNavController.navigate(Screen.RecipeTab.route) {
                    popUpTo(homeNavController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                sharedViewModel.resetRecipeGenerationState()
                viewModel.setDeselectedFoods(filteredFoods.toSet())
            }

            is RecipeGenerationState.Error -> {
                sharedViewModel.resetRecipeGenerationState()
            }

            RecipeGenerationState.Idle -> Unit
        }
    }

    // 화면
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FridgeSearchSection(
            value = typingQuery,
            onValueChange = { viewModel.updateTypingQuery(it) },
            onSearchConfirmed = { viewModel.updateFilterQuery(typingQuery) }
        )

        FoodTypeFilter(
            types = Type.entries,
            selectedType = selectedType,
            onTypeSelected = { viewModel.updateSelectedType(it) }
        )

        FoodListSection(
            mode = mode,
            foods = filteredFoods,
            fridgeState = state,
            selectedFoods = selectedFoods,
            onFoodSelectToggle = {
                viewModel.toggleFoodSelected(it)
            },
            onClickFood = {
                sharedViewModel.setEditFood(it, EditSource.HOME)
                navController.navigate(Screen.EditFoodScreen.route)
            },
            onClickMinus = {
                if (it.count == 1) viewModel.deleteFood(it)
                else viewModel.updateFood(it.copy(count = it.count - 1))
            },
            onClickPlus = {
                viewModel.updateFood(it.copy(count = it.count + 1))
            },
            onRefreshClick = {
                viewModel.loadFoods()
            }
        )
    }
}