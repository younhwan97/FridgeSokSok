package com.yh.fridgesoksok.presentation.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.camera.CameraScreen
import com.yh.fridgesoksok.presentation.food_list.FoodListScreen
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionButton
import com.yh.fridgesoksok.presentation.home.fab.FloatingActionMenus
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var searchQuery by remember { mutableStateOf("") }

    // FAB
    var fabOffset by remember { mutableStateOf(Offset.Zero) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }
    BackHandler(enabled = isFabMenuExpanded) {
        isFabMenuExpanded = false
    }

    // UI
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CustomBackGroundColor,
                        titleContentColor = CustomPrimaryColor,
                        scrolledContainerColor = CustomLightGrayBackGroundColor
                    ),
                    title = {
                        Column {
                            Text(
                                text = "냉장고속속",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = if (scrollBehavior.state.collapsedFraction > 0.5) 8.sp else 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(1f - min(1f, scrollBehavior.state.collapsedFraction * 2))
                            )

                            Spacer(Modifier.height(4.dp))

                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {}
                                ),
                                maxLines = 1,
                                decorationBox = @Composable { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.Gray.copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .padding(horizontal = 16.dp)  // 전체 여백 추가
                                    ) {
                                        // 검색 아이콘
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search Icon",
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)  // 아이콘을 왼쪽에 정렬
                                                .padding(end = 8.dp)  // 아이콘과 텍스트 사이 여백
                                        )

                                        // 텍스트 필드를 가운데 정렬
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Center)  // 텍스트 필드를 가운데 정렬
                                                .fillMaxWidth()  // 텍스트가 차지할 수 있는 공간을 확장
                                                .padding(start = 32.dp)  // 아이콘과 텍스트 사이의 여백 조정
                                        ) {
                                            innerTextField()
                                        }
                                    }
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { /* do something */ },
                            modifier = Modifier.alpha(0f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                CustomBottomNavigation()
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = CustomBackGroundColor)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FoodListScreen()
            }
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
            expanded = isFabMenuExpanded,
            onClick = { isFabMenuExpanded = !isFabMenuExpanded },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    fabOffset = coordinates.positionInRoot()
                }
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(end = 16.dp, bottom = 72.dp)
                .zIndex(1f)
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