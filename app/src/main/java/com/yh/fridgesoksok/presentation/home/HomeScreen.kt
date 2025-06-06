package com.yh.fridgesoksok.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.home.comp.HomeBottomBar
import com.yh.fridgesoksok.presentation.home.comp.HomeFabBar
import com.yh.fridgesoksok.presentation.home.comp.HomeNavGraph
import com.yh.fridgesoksok.presentation.home.comp.HomeTopAppBar

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiMode by homeViewModel.uiMode.collectAsState()
    var isFabExpanded by remember { mutableStateOf(false) }
    val homeNavController = rememberNavController()
    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route ?: Screen.FridgeTab.route

    // 뒤로가기 핸들링
    BackHandler(enabled = isFabExpanded || homeUiMode == HomeUiMode.RECIPE_SELECT) {
        when {
            isFabExpanded -> {
                // FAB 오픈 상태에서 뒤로가기 시 FAB 닫기
                isFabExpanded = false
            }

            homeUiMode == HomeUiMode.RECIPE_SELECT -> {
                // 레시피 모드에서 뒤로가기 시 DEFAULT 모드로 전환
                homeViewModel.resetUiMode()
            }
        }
    }

    // 화면
    Scaffold(
        topBar = {
            HomeTopAppBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                onClickAiRecipeBtn = { homeViewModel.updateToRecipeUiMode() },
                onClickNavigationBtn = { homeViewModel.updateToDefaultUiMode() },
                onClickSelectAll = { sharedViewModel.requestSelectAllFoods() },
                onClickDeselectAll = { sharedViewModel.requestDeselectAllFoods() }
            )
        },
        bottomBar = {
            HomeBottomBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                homeNavController = homeNavController
            )
        },
        floatingActionButton = {
            HomeFabBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                isFabMenuExpanded = isFabExpanded,
                onToggleFab = { isFabExpanded = !isFabExpanded },
                onCaptureClick = { navController.navigate(Screen.CameraScreen.route) },
                onUploadClick = { },
                onManualClick = { navController.navigate(Screen.UploadScreen.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.padding(padding)) {
            // Home Nav (냉장고/레시피/계정)
            HomeNavGraph(
                mode = homeUiMode,
                homeNavController = homeNavController,
                navController = navController,
                sharedViewModel = sharedViewModel,
            )

            // FAB Overlay Screen
            if (currentRoute == Screen.FridgeTab.route && isFabExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable { isFabExpanded = false }
                )
            }
        }
    }
}