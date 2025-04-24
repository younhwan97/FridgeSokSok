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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.food_list.FoodListScreen
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionButton
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionMenus
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    // FAB
    var fabOffset by remember { mutableStateOf(Offset.Zero) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }
    BackHandler(enabled = isFabMenuExpanded) {
        isFabMenuExpanded = false
    }

    // UI
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "냉장고 속속") })
            },
            bottomBar = {
                CustomBottomNavigation()
            }
        ) { innerPadding ->
            FoodListScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }

        // ------------------ FAB(Floating Action Button) ------------------ //
        // FAB Overlay Screen
        if (isFabMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { isFabMenuExpanded = false }
            )
        }

        // FAB
        FloatingActionButton(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    // FAB 위치 추출
                    fabOffset = coordinates.positionInRoot()
                }
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(end = 16.dp, bottom = 72.dp)
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
            onCaptureClick = {
                navController.navigate(Screen.CameraScreen.route)
            },
            onUploadClick = {},
            onManualClick = {
                navController.navigate(Screen.UploadScreen.route)
            },
        )
    }
}

@Composable
fun CustomBottomNavigation() {
    val bottomInset =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = Color(color = 0xFFF8F8F8),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp + bottomInset)
            .padding(bottom = bottomInset),

        ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "홈",
                        tint = Color.Black
                    )
                    Text(
                        text = "홈",
                        color = Color.Black,
                        fontSize = 10.sp
                    )
                }
            }
            IconButton(
                onClick = { },
                modifier = Modifier.alpha(0f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "홈",
                        tint = Color.Black
                    )
                    Text(
                        text = "홈",
                        color = Color.Black,
                        fontSize = 10.sp
                    )
                }
            }
            IconButton(
                onClick = { }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "홈",
                        tint = Color.Black
                    )
                    Text(
                        text = "계정",
                        color = Color.Black,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}