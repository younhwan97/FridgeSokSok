package com.yh.fridgesoksok.presentation.fridge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.fridge.comp.FoodListSection
import com.yh.fridgesoksok.presentation.fridge.comp.FoodTypeFilter
import com.yh.fridgesoksok.presentation.fridge.comp.FridgeSearchSection
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.home.HomeViewModel
import com.yh.fridgesoksok.presentation.model.Type

private const val KEY_FOOD = "food"
private const val KEY_SOURCE = "source"
private const val SOURCE_HOME = "HOME"
private const val MIN_COUNT = 1

@Composable
fun FridgeScreen(
    mode: HomeUiMode,
    navController: NavController,
    homeNavController: NavController,
    viewModel: FridgeViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // ViewModel 상태 구독
    val state by viewModel.state.collectAsState()
    val typingQuery by viewModel.typingQuery.collectAsState() // 사용자가 입력 중인 검색어 (즉시 반영 X)
    val selectedType by viewModel.selectedType.collectAsState() // 음식 타입 필터 (냉동, 유제품 등)
    val filteredFoods by viewModel.filteredFoods.collectAsState() // 필터링된 음식 목록 (검색어 + 타입 필터 기준)
    val selectedFoods by viewModel.selectedFoods.collectAsState() // 선택된 음식 목록 (레시피 생성 시 사용)

    // 화면 재진입 시 데이터 로드
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadFoods()
        }
    }

    // 전체 선택 처리
    LaunchedEffect(Unit) {
        homeViewModel.selectAllTrigger.collect {
            viewModel.selectAllFoods()
        }
    }

    // 전체 해제 처리
    LaunchedEffect(Unit) {
        homeViewModel.deselectAllTrigger.collect {
            viewModel.deselectAllFoods()
        }
    }

    // 레시피 생성 트리거 이벤트
    LaunchedEffect(Unit) {
        homeViewModel.generateRecipeTrigger.collect {
            // FridgeViewModel의 generateRecipe 호출하면서 상태 변경을 HomeViewModel에 보고
            viewModel.generateRecipe { state ->
                homeViewModel.updateRecipeGenerationState(state)
            }
        }
    }

    // 레시피 생성 결과 처리
    LaunchedEffect(Unit) {
        viewModel.recipeGenerationEvent.collect { event ->
            when (event) {
                is RecipeGenerationEvent.Success -> {
                    // RecipeTab으로 이동
                    homeNavController.navigate(Screen.RecipeTab.route) {
                        popUpTo(homeNavController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
                is RecipeGenerationEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // 화면
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FridgeSearchSection(
                value = typingQuery,
                onValueChange = viewModel::updateTypingQuery,
                onSearchConfirmed = { viewModel.updateFilterQuery(typingQuery) }
            )

            FoodTypeFilter(
                types = Type.entries,
                selectedType = selectedType,
                onTypeSelected = viewModel::updateSelectedType
            )

            FoodListSection(
                mode = mode,
                foods = filteredFoods,
                fridgeState = state,
                selectedFoods = selectedFoods,
                onFoodSelectToggle = viewModel::toggleFoodSelected,
                onClickFood = { food ->
                    navigateToEditFood(
                        navController = navController,
                        food = food
                    )
                },
                onClickMinus = { food ->
                    if (food.count == MIN_COUNT) {
                        viewModel.deleteFood(food)
                    } else {
                        viewModel.updateFood(food.copy(count = food.count - 1))
                    }
                },
                onClickPlus = { food ->
                    viewModel.updateFood(food.copy(count = food.count + 1))
                },
                onRefreshClick = viewModel::loadFoods
            )
        }

        // 스낵바
        Snackbar(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.BottomCenter),
            snackBarHostState = snackbarHostState
        )
    }
}

/**
 * EditFood 화면으로 네비게이션
 */
private fun navigateToEditFood(
    navController: NavController,
    food: com.yh.fridgesoksok.presentation.model.FoodModel
) {
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set(KEY_FOOD, food)
        set(KEY_SOURCE, SOURCE_HOME)
    }
    navController.navigate(Screen.EditFoodScreen.route)
}