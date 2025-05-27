package com.yh.fridgesoksok.presentation.fridge

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.SearchBar
import com.yh.fridgesoksok.presentation.fridge.comp.FoodListSection
import com.yh.fridgesoksok.presentation.fridge.comp.FoodTypeFilter
import com.yh.fridgesoksok.presentation.model.Type

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FridgeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: FridgeViewModel = hiltViewModel()
) {
    val fridgeState by viewModel.state.collectAsState()
    val foods by viewModel.foods.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    var input by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 입력창이 비면 자동으로 검색 조건도 초기화
    LaunchedEffect(input) {
        if (input.isBlank()) {
            viewModel.updateSearchQuery("")
        }
    }

    // 새로고침 (화면 돌아올 때마다 loadFoods)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadFoods()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Content
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FridgeSearchSection(
            value = input,
            onValueChange = { input = it },
            onSearchConfirmed = {
                viewModel.updateSearchQuery(input)
            }
        )

        FoodTypeFilter(
            types = Type.entries,
            selectedType = selectedType,
            onTypeSelected = { viewModel.updateSelectedType(it) }
        )

        FoodListSection(
            foods = foods,
            fridgeState = fridgeState,
            searchQuery = searchQuery,
            selectedType = selectedType,
            scrollState = scrollState,
            onClickCard = { food ->
                sharedViewModel.setEditFood(food, EditSource.HOME)
                navController.navigate(Screen.EditFoodScreen.route)
            },
            onClickMinus = { food ->
                if (food.count == 1) viewModel.deleteFood(food)
                else viewModel.updateFood(food.copy(count = food.count - 1))
            },
            onClickPlus = { food ->
                viewModel.updateFood(food.copy(count = food.count + 1))
            }
        )
    }
}

@Composable
private fun FridgeSearchSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchConfirmed: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        value = value,
        onValueChange = {
            if (it.length <= 30) onValueChange(it)
        },
        onDone = {
            onSearchConfirmed()
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    )
}
