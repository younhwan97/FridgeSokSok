package com.yh.fridgesoksok.presentation.fridge

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.EditSource
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.SearchBar
import com.yh.fridgesoksok.presentation.fridge.comp.FoodListContent
import com.yh.fridgesoksok.presentation.fridge.comp.FoodTypeChips
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    sharedViewModel: SharedViewModel,
    viewModel: FridgeViewModel = hiltViewModel()
) {
    val fridgeState by viewModel.state.collectAsState()
    val foods by viewModel.foods.collectAsState()
    var input by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(Type.All) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current

    if (input.isBlank()) searchQuery = ""

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadFoods()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = modifier) {
        SearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = input,
            onValueChange = { if (it.length < 30) input = it },
            onDone = {
                searchQuery = input
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FoodTypeChips(
            types = Type.entries,
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FoodListContent(
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