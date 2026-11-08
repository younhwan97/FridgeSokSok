package com.yh.fridgesoksok.presentation.home

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.common.comp.ConfirmDialog
import com.yh.fridgesoksok.presentation.common.constants.StringResources.DialogMessages.USE_THIS_PHOTO
import com.yh.fridgesoksok.presentation.common.util.rememberActionCooldown
import com.yh.fridgesoksok.presentation.common.util.rememberCameraLauncher
import com.yh.fridgesoksok.presentation.common.util.rememberGalleryPickerLauncher
import com.yh.fridgesoksok.presentation.common.util.rememberNotificationPermissionLauncher
import com.yh.fridgesoksok.presentation.common.util.uriToBitmap
import com.yh.fridgesoksok.presentation.fridge.RecipeGenerationState
import com.yh.fridgesoksok.presentation.home.comp.HomeBottomBar
import com.yh.fridgesoksok.presentation.home.comp.HomeFabBar
import com.yh.fridgesoksok.presentation.home.comp.HomeFabOverlay
import com.yh.fridgesoksok.presentation.home.comp.HomeGeneratingRecipe
import com.yh.fridgesoksok.presentation.home.comp.HomeNavGraph
import com.yh.fridgesoksok.presentation.home.comp.HomeTopAppBar
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    // 하단 바 색상 설정
    SideEffect {
        systemUiController.setNavigationBarColor(CustomGreyColor1)
    }

    // Home 라우팅 (Fridge, Recipe, Account)
    val homeNavController = rememberNavController()
    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
        ?: Screen.FridgeTab.route

    // ViewModel 상태 구독
    val homeUiMode by homeViewModel.uiMode.collectAsState() // HomeUI 모드 (Default, Recipe 재료선택)
    val recipeGenerationState by homeViewModel.recipeGenerationState.collectAsState() // 레시피 생성 상태
    val isRecipeGenerationAvailable by homeViewModel.isRecipeGenerationAvailable.collectAsState() // 레시피 생성 API 사용 가능 여부

    // UI 상태
    var isFabExpanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown()

    // 알림, 갤러리, 카메라 런처
    val launchNotification = rememberNotificationPermissionLauncher(context) { }
    val launchCamera = rememberCameraLauncher(context) {
        navController.navigate(Screen.CameraScreen.route)
    }
    val launchGallery = rememberGalleryPickerLauncher { uri ->
        selectedImageUri = uri
        showConfirmDialog = true
    }

    // 알림 권한 요청
    LaunchedEffect(Unit) {
        launchNotification()
    }

    // 화면 변경 시 UI 모드 리셋
    LaunchedEffect(currentRoute) {
        homeViewModel.resetUiMode()
    }

    // 뒤로가기 처리
    BackHandler(isFabExpanded || homeUiMode == HomeUiMode.RECIPE_SELECT) {
        when {
            // FAB 오픈 상태에서 뒤로가기 시 FAB 닫기
            isFabExpanded -> isFabExpanded = false
            // 레시피 재료선택 모드에서 뒤로가기 시 HomeUI 모드 리셋
            homeUiMode == HomeUiMode.RECIPE_SELECT -> homeViewModel.resetUiMode()
        }
    }

    // 화면
    Scaffold(
        topBar = {
            HomeTopAppBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                recipeGenerationState = recipeGenerationState,
                onClickAiRecipeBtn = { homeViewModel.updateToRecipeUiMode() },
                onClickNavigationBtn = {
                    homeViewModel.updateToDefaultUiMode()
                    homeViewModel.triggerDeselectAll()
                },
                onClickSelectAll = homeViewModel::triggerSelectAll,
                onClickDeselectAll = homeViewModel::triggerDeselectAll
            )
        },
        bottomBar = {
            HomeBottomBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                homeNavController = homeNavController,
                recipeGenerationState = recipeGenerationState,
                isRecipeGenerationAvailable = isRecipeGenerationAvailable,
                onClickGenerateRecipe = homeViewModel::triggerGenerateRecipe,
                onClickGenerateRecipeFromFridge = homeViewModel::triggerGenerateRecipeFromFridge
            )
        },
        floatingActionButton = {
            HomeFabBar(
                mode = homeUiMode,
                currentRoute = currentRoute,
                isFabMenuExpanded = isFabExpanded,
                onToggleFab = { isFabExpanded = !isFabExpanded },
                onCaptureClick = {
                    triggerCooldown()
                    launchCamera()
                },
                onUploadClick = {
                    triggerCooldown()
                    launchGallery()
                },
                onManualClick = {
                    triggerCooldown()
                    navController.navigate(Screen.UploadScreen.route)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            HomeNavGraph(
                mode = homeUiMode,
                homeNavController = homeNavController,
                navController = navController,
                homeViewModel = homeViewModel,
                sharedViewModel = sharedViewModel
            )
        }

        // FAB 오버레이
        if (isFabExpanded) {
            HomeFabOverlay(
                currentRoute = currentRoute,
                onClickFabOverlay = { isFabExpanded = false }
            )
        }

        // 갤러리에서 이미지 선택 후 확인 다이얼로그
        if (showConfirmDialog) {
            ConfirmDialog(
                message = USE_THIS_PHOTO,
                onConfirm = {
                    selectedImageUri?.let { uri ->
                        val bitmap = uriToBitmap(context, uri)
                        if (bitmap != null) {
                            // 공유 뷰모델 영수증 이미지 셋팅
                            sharedViewModel.setReceipt(bitmap)
                            // Upload 화면으로 이동
                            navController.navigate(Screen.UploadScreen.route)
                        }
                    }
                    showConfirmDialog = false
                },
                onDismiss = { showConfirmDialog = false }
            )
        }

        // 레시피 생성 오버레이
        if (recipeGenerationState == RecipeGenerationState.Loading) {
            HomeGeneratingRecipe()
        }

        // Blocking 화면을 이용해 터치 제어
        if (!canTrigger) {
            BlockingLoadingOverlay(showLoading = false)
        }
    }
}