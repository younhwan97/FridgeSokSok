package com.yh.fridgesoksok.presentation.fridge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.RecipeGenerationState
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.comp.Snackbar
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
    val snackbarHostState = remember { SnackbarHostState() }
    // 사용자가 입력 중인 검색어 (즉시 반영 X)
    val typingQuery by viewModel.typingQuery.collectAsState()
    // 선택된 음식 타입 필터 (냉동, 유제품 등)
    val selectedType by viewModel.selectedType.collectAsState()
    // 필터링된 음식 목록 (검색어 + 타입 필터 기준)
    val filteredFoods by viewModel.filteredFoods.collectAsState()
    // 선택된 음식 목록 (레시피 생성에서 사용)
    val selectedFoods by viewModel.selectedFoods.collectAsState()
    // 레시피 생성 상태 (Idle / Loading / Success / Error)
    val recipeGenerationState by sharedViewModel.recipeGenerationState.collectAsState()

    // 화면 재진입 시 데이터 로드
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadFoods()
        }
    }

    // 전체선택 처리
    LaunchedEffect(Unit) {
        sharedViewModel.selectAllFoodsRequested.collect {
            viewModel.setSelectedFoods(filteredFoods.toSet())
        }
    }

    // 전체해제 처리
    LaunchedEffect(Unit) {
        sharedViewModel.deselectAllFoodsRequested.collect {
            viewModel.setDeselectedFoods(filteredFoods.toSet())
        }
    }

    // "SharedViewModel" 레시피 생성 상태 감지 및 처리
    LaunchedEffect(recipeGenerationState) {
        when (recipeGenerationState) {
            // 1. 레피시 생성 요청
            is RecipeGenerationState.Loading ->
                // Home 화면에서 레시피 생성 상태가 Idle → Loading으로 전환되면 이를 감지해 즉시 레시피 생성 API를 호출함
                viewModel.createRecipe(selectedFoods) { success, errorMsg ->
                    // 성공/실패 상태를 SharedViewModel에 반영
                    // 성공 시 "2. 레시피 생성 성공" 수행
                    // 실패 시 "3. 레시피 생성 실패" 수행
                    sharedViewModel.completeRecipeGeneration(success = success, errorMessage = errorMsg)
                }

            // 2. 레시피 생성 성공
            is RecipeGenerationState.Success -> {
                // RecipeTab으로 이동
                homeNavController.navigate(Screen.RecipeTab.route) {
                    popUpTo(homeNavController.graph.findStartDestination().id) {
                        saveState = true
                        inclusive = false
                    }
                    restoreState = true
                    launchSingleTop = true
                }
                sharedViewModel.resetRecipeGenerationState()
                viewModel.setDeselectedFoods(filteredFoods.toSet())
            }

            // 3. 레시피 생성 실패
            is RecipeGenerationState.Error -> {
                snackbarHostState.showSnackbar(message = (recipeGenerationState as RecipeGenerationState.Error).message.toString())
                sharedViewModel.resetRecipeGenerationState()
            }

            // 0. 대기 상태
            is RecipeGenerationState.Idle -> Unit
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

        // 스낵바
        Snackbar(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.BottomCenter),
            snackBarHostState = snackbarHostState
        )
    }
}