package com.yh.fridgesoksok.presentation.home

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog
import com.yh.fridgesoksok.presentation.common.util.rememberCameraLauncher
import com.yh.fridgesoksok.presentation.common.util.rememberGalleryPickerLauncher
import com.yh.fridgesoksok.presentation.common.util.rememberNotificationPermissionLauncher
import com.yh.fridgesoksok.presentation.common.util.uriToBitmap
import com.yh.fridgesoksok.presentation.home.comp.HomeBottomBar
import com.yh.fridgesoksok.presentation.home.comp.HomeFabBar
import com.yh.fridgesoksok.presentation.home.comp.HomeFabOverlay
import com.yh.fridgesoksok.presentation.home.comp.HomeGeneratingRecipe
import com.yh.fridgesoksok.presentation.home.comp.HomeNavGraph
import com.yh.fridgesoksok.presentation.home.comp.HomeTopAppBar

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeNavController = rememberNavController()
    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route ?: Screen.FridgeTab.route

    val homeUiMode by homeViewModel.uiMode.collectAsState()
    val recipeState by sharedViewModel.recipeGenerationState.collectAsState()

    var isFabExpanded by remember { mutableStateOf(false) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val launchGallery = rememberGalleryPickerLauncher(context) { uri ->
        selectedImageUri.value = uri
        showConfirmDialog = true
    }

    val launchCamera = rememberCameraLauncher(context) {
        navController.navigate(Screen.CameraScreen.route)
    }

    val launchNotification = rememberNotificationPermissionLauncher(context)

    // 화면 변경 시 UI 모드 리셋
    LaunchedEffect(currentRoute) {
        homeViewModel.resetUiMode()
    }

    // 알림 권한 요청
    LaunchedEffect(Unit) {
        launchNotification()
    }

    // 뒤로가기 핸들링
    BackHandler(isFabExpanded || homeUiMode == HomeUiMode.RECIPE_SELECT) {
        when {
            // FAB 오픈 상태에서 뒤로가기 시 FAB 닫기
            isFabExpanded -> isFabExpanded = false
            // 레시피 모드에서 뒤로가기 시 UI 모드 리셋
            homeUiMode == HomeUiMode.RECIPE_SELECT -> homeViewModel.resetUiMode()
        }
    }

    // 화면
    Scaffold(
        topBar = {
            HomeTopAppBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                recipeGenerationState = recipeState,
                onClickAiRecipeBtn = { homeViewModel.updateToRecipeUiMode() },
                onClickNavigationBtn = {
                    homeViewModel.updateToDefaultUiMode()
                    sharedViewModel.resetRecipeGenerationState()
                },
                onClickSelectAll = { sharedViewModel.requestSelectAllFoods() },
                onClickDeselectAll = { sharedViewModel.requestDeselectAllFoods() }
            )
        },
        bottomBar = {
            HomeBottomBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                homeNavController = homeNavController,
                recipeGenerationState = recipeState,
                onClickGenerateRecipe = { sharedViewModel.startRecipeGeneration() }
            )
        },
        floatingActionButton = {
            HomeFabBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                isFabMenuExpanded = isFabExpanded,
                onToggleFab = { isFabExpanded = !isFabExpanded },
                onCaptureClick = { launchCamera() },
                onUploadClick = { launchGallery() },
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

            // FAB 오버레이 화면
            HomeFabOverlay(
                currentRoute = currentRoute,
                isFabExpanded = isFabExpanded,
                onClickFabOverlay = { isFabExpanded = false }
            )

            // Recipe 생성 로딩 화면
            HomeGeneratingRecipe(
                recipeGenerateState = recipeState
            )
        }

        if (showConfirmDialog) {
            ConfirmDialog(
                message = "이 사진을 사용 할까요?",
                onConfirm = {
                    val bitmap = uriToBitmap(context, selectedImageUri.value!!)
                    if (bitmap != null) {
                        sharedViewModel.setReceipt(bitmap)
                        navController.navigate(Screen.UploadScreen.route)
                    }
                    showConfirmDialog = false
                },
                onDismiss = { showConfirmDialog = false }
            )
        }
    }
}