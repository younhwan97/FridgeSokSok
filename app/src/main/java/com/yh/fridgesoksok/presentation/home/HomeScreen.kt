package com.yh.fridgesoksok.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.account.AccountScreen
import com.yh.fridgesoksok.presentation.fridge.FridgeScreen
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionButton
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionMenus
import com.yh.fridgesoksok.presentation.recipe.RecipeScreen
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val homeNavController = rememberNavController()
    val systemUiController = rememberSystemUiController()
    var isFabMenuExpanded by remember { mutableStateOf(false) }
    val currentRoute by homeNavController.currentBackStackEntryAsState()
        .let { derivedStateOf { it.value?.destination?.route ?: Screen.FridgeTab.route } }

    // 시스템 UI 설정
    SideEffect {
        systemUiController.setNavigationBarColor(color = Color.White)
    }

    // FAB 열려있으면 FAB 닫기
    BackHandler(enabled = isFabMenuExpanded) {
        isFabMenuExpanded = false
    }

    // Content
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopAppBar() },
        bottomBar = { HomeBottomNavigation(currentRoute, homeNavController) },
        floatingActionButton = {
            if (currentRoute == Screen.FridgeTab.route) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FloatingActionMenus(
                        expanded = isFabMenuExpanded,
                        onCaptureClick = { navController.navigate(Screen.CameraScreen.route) },
                        onUploadClick = {},
                        onManualClick = { navController.navigate(Screen.UploadScreen.route) }
                    )

                    FloatingActionButton(
                        modifier = Modifier
                            .zIndex(1f)
                            .align(Alignment.End),
                        expanded = isFabMenuExpanded,
                        onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            HomeTabNavHost(
                homeNavController = homeNavController,
                mainNavController = navController,
                sharedViewModel = sharedViewModel
            )

            // FAB Overlay Screen
            if (currentRoute == Screen.FridgeTab.route && isFabMenuExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable { isFabMenuExpanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.logo02),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        },
        actions = {
            Image(
                modifier = Modifier.padding(end = 12.dp),
                painter = painterResource(R.drawable.ai),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun HomeTabNavHost(
    homeNavController: NavHostController,
    mainNavController: NavController,
    sharedViewModel: SharedViewModel
) {
    NavHost(
        navController = homeNavController,
        startDestination = Screen.FridgeTab.route
    ) {
        composable(Screen.FridgeTab.route) {
            FridgeScreen(
                modifier = Modifier.fillMaxSize(),
                navController = mainNavController,
                sharedViewModel = sharedViewModel
            )
        }
        composable(Screen.RecipeTab.route) {
            RecipeScreen()
        }
        composable(Screen.AccountTab.route) {
            AccountScreen()
        }
    }
}

@Composable
fun HomeBottomNavigation(
    currentRoute: String,
    homeNavController: NavHostController
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.bottomNavItems.forEach { screen ->
                val selected = currentRoute == screen.route
                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable {
                            if (!selected) {
                                homeNavController.navigate(screen.route) {
                                    popUpTo(homeNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    screen.selectedIcon?.let {
                        Image(
                            painter = painterResource(if (selected) screen.selectedIcon else screen.unselectedIcon!!),
                            contentDescription = null,
                            contentScale = ContentScale.None
                        )
                    }
                    screen.label?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) CustomGreyColor7 else CustomGreyColor5
                        )
                    }
                }
            }
        }
    }
}