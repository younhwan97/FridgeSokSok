package com.yh.fridgesoksok.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.food_list.FoodListScreen
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionButton
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionMenus

@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeNavController = rememberNavController()
    var fabOffset by remember { mutableStateOf(Offset.Zero) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // Track current tab
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // FAB 열려있으면 FAB 닫기
    BackHandler(enabled = isFabMenuExpanded) { isFabMenuExpanded = false }

    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { CustomBottomNavigation(homeNavController) }
    ) { padding ->
        BoxWithConstraints(Modifier.padding(padding)) {
            NavHost(
                navController = homeNavController,
                startDestination = "home",
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                composable("home") {
                    FoodListScreen(modifier = Modifier.fillMaxSize())
                }
                composable("recipe") {
                    Column(modifier = Modifier.fillMaxSize()) {}
                }
                composable("account") {
                    Column(modifier = Modifier.fillMaxSize()) {}
                }
            }

            // ------------------ FAB(Floating Action Button) ------------------ //
            if (currentRoute == "home") {
                // FAB Overlay Screen
                if (isFabMenuExpanded)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { isFabMenuExpanded = false }
                    )

                // FAB
                FloatingActionButton(
                    modifier = Modifier
                        .onGloballyPositioned { fabOffset = it.positionInRoot() } // FAB 위치 추출
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .zIndex(1f),
                    expanded = isFabMenuExpanded,
                    onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                )

                // FAB Menu
                FloatingActionMenus(
                    expanded = isFabMenuExpanded,
                    fabOffset = fabOffset,
                    screenWidth = constraints.maxWidth.toFloat(),
                    screenHeight = constraints.maxHeight.toFloat(),
                    menuWidthDp = 186.dp,
                    menuHeightDp = 150.dp,
                    onCaptureClick = { navController.navigate(Screen.CameraScreen.route) },
                    onUploadClick = {},
                    onManualClick = { navController.navigate(Screen.UploadScreen.route) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "냉장고 속속"
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun CustomBottomNavigation(
    homeNavController: NavHostController
) {
    val items = listOf(
        Triple("home", Icons.Default.Home, "냉장고"),
        Triple("recipe", Icons.Default.Person, "레시피"),
        Triple("account", Icons.Default.Person, "계정")
    )
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
            items.forEach { (route, icon, label) ->
                IconButton(onClick = {
                    if (route != currentRoute) {

                    }
                }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (currentRoute == route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = if (currentRoute == route)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}