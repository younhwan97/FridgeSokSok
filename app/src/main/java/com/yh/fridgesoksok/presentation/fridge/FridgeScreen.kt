package com.yh.fridgesoksok.presentation.fridge

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
import com.yh.fridgesoksok.presentation.common.constants.NavigationConstants.KEY_ADDED_FOOD_COUNT
import com.yh.fridgesoksok.presentation.common.constants.NavigationConstants.KEY_FOOD
import com.yh.fridgesoksok.presentation.common.constants.NavigationConstants.KEY_SOURCE
import com.yh.fridgesoksok.presentation.common.constants.NavigationConstants.MIN_COUNT
import com.yh.fridgesoksok.presentation.common.constants.NavigationConstants.SOURCE_HOME
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ButtonText.DELETE
import com.yh.fridgesoksok.presentation.common.constants.StringResources.DialogMessages.DELETE_FOOD_CONFIRM
import com.yh.fridgesoksok.presentation.common.constants.StringResources.InfoMessages.foodsAdded
import com.yh.fridgesoksok.presentation.fridge.comp.FoodListSection
import com.yh.fridgesoksok.presentation.fridge.comp.FoodTypeFilter
import com.yh.fridgesoksok.presentation.fridge.comp.FridgeSearchSection
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.home.HomeViewModel
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomErrorColor
import kotlinx.coroutines.launch

@Composable
fun FridgeScreen(
    mode: HomeUiMode,
    navController: NavController,
    homeNavController: NavController,
    viewModel: FridgeViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // 수량이 1개일 때 - 를 누르면 삭제 전에 확인 (화면 회전 시에도 유지)
    var foodPendingDelete by rememberSaveable { mutableStateOf<FoodModel?>(null) }

    // ViewModel 상태 구독
    val state by viewModel.state.collectAsState()
    val typingQuery by viewModel.typingQuery.collectAsState() // 사용자가 입력 중인 검색어 (즉시 반영 X)
    val selectedType by viewModel.selectedType.collectAsState() // 음식 타입 필터 (냉동, 유제품 등)
    val filteredFoods by viewModel.filteredFoods.collectAsState() // 필터링된 음식 목록 (검색어 + 타입 필터 기준)
    val selectedFoods by viewModel.selectedFoods.collectAsState() // 선택된 음식 목록 (레시피 생성 시 사용)

    // 화면 최초 진입 시 데이터 로드
    LaunchedEffect(Unit) {
        viewModel.loadFoods()
    }

    // 업로드 화면에서 식품을 추가하고 돌아오면 완료 안내 (한 번만 표시)
    LaunchedEffect(Unit) {
        val handle = navController.currentBackStackEntry?.savedStateHandle ?: return@LaunchedEffect
        handle.getStateFlow<Int?>(KEY_ADDED_FOOD_COUNT, null).collect { count ->
            if (count != null) {
                handle.remove<Int>(KEY_ADDED_FOOD_COUNT)
                snackbarHostState.showSnackbar(foodsAdded(count))
            }
        }
    }

    // HomeViewModel 트리거 및 이벤트 구독 (병렬 처리)
    LaunchedEffect(Unit) {
        // 전체 선택 트리거
        launch {
            homeViewModel.selectAllTrigger.collect {
                viewModel.selectAllFoods()
            }
        }

        // 전체 해제 트리거
        launch {
            homeViewModel.deselectAllTrigger.collect {
                viewModel.deselectAllFoods()
            }
        }

        // 레시피 생성 트리거
        launch {
            homeViewModel.generateRecipeTrigger.collect {
                // FridgeViewModel의 generateRecipe 호출하면서 상태 변경을 HomeViewModel에 보고
                viewModel.generateRecipe { state ->
                    homeViewModel.updateRecipeGenerationState(state)
                }
            }
        }

        // 자동 레시피 생성 트리거
        launch {
            homeViewModel.generateRecipeFromFridgeTrigger.collect {
                // FridgeViewModel의 generateRecipeFromFridge 호출하면서 상태 변경을 HomeViewModel에 보고
                viewModel.generateRecipeFromFridge { state ->
                    homeViewModel.updateRecipeGenerationState(state)
                }
            }
        }

        // 식품 로드/수정/삭제 실패 알림
        launch {
            viewModel.errorMessage.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        // 레시피 생성 결과 처리
        launch {
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
                types = Type.selectableEntries,
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
                        foodPendingDelete = food
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

        // 식품 삭제 확인 다이얼로그
        foodPendingDelete?.let { food ->
            ConfirmDialog(
                title = food.itemName,
                message = DELETE_FOOD_CONFIRM,
                confirmText = DELETE,
                confirmTextColor = CustomErrorColor,
                confirmContainerColor = MaterialTheme.colorScheme.errorContainer,
                onConfirm = {
                    viewModel.deleteFood(food)
                    foodPendingDelete = null
                },
                onDismiss = {
                    foodPendingDelete = null
                }
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
    food: FoodModel
) {
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set(KEY_FOOD, food)
        set(KEY_SOURCE, SOURCE_HOME)
    }
    navController.navigate(Screen.EditFoodScreen.route)
}
