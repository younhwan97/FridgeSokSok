package com.yh.fridgesoksok.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.presentation.food_list.FoodListScreen
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var searchQuery by remember { mutableStateOf("") }
    
    // FAB(Floating Action Button)의 위치 정보
    var fabOffset by remember { mutableStateOf(Offset.Zero) }
    // FAB Menu 확장 여부
    var isFabMenuExpanded by remember { mutableStateOf(false) }

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

        // FAB(Floating Action Button)
        ExtendedFloatingActionButton(
            onClick = { isFabMenuExpanded = !isFabMenuExpanded },
            shape = CircleShape,
            modifier = Modifier
                .zIndex(2f)
                .onGloballyPositioned { coordinates ->
                    fabOffset = coordinates.positionInRoot()
                }
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.navigationBars) // ⬅︎ 하단 시스템 UI 고려
                .padding(end = 16.dp, bottom = 16.dp) // ⬅︎ 추가 마진 (선택사항)
        ) {
            Icon(
                imageVector = if (isFabMenuExpanded) Icons.Default.Close else Icons.Default.Edit,
                contentDescription = null
            )

            if (!isFabMenuExpanded) {
                Text(
                    text = "추가하기",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Overlay Screen
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val menuWidth = with(LocalDensity.current) { 186.dp.toPx() }
        val menuHeight = with(LocalDensity.current) { 120.dp.toPx() }

        if (isFabMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { isFabMenuExpanded = false } // 배경 클릭 시 닫기
            )
        }

        AnimatedVisibility(
            visible = isFabMenuExpanded,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier
                .zIndex(1f)
                .absoluteOffset {
                    val fabX = fabOffset.x
                    val fabY = fabOffset.y

                    // X 좌표 보정: 메뉴가 오른쪽으로 삐져나가는 경우 왼쪽으로 당김
                    val offsetX = when {
                        fabX + menuWidth > screenWidth -> (screenWidth - menuWidth).toInt()
                        else -> (fabX - menuWidth + 100.dp.toPx()).toInt()
                    }

                    // Y 좌표 보정: FAB 위로 띄우되, 너무 위면 아래로 이동
                    val offsetY = if (fabY - menuHeight > 0) {
                        (fabY - menuHeight).toInt()
                    } else {
                        // 위에 공간 없으면 아래로
                        (fabY + 100.dp.toPx()).toInt()
                    }

                    IntOffset(offsetX - 32, offsetY)
                }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .width(186.dp)
                    .height(100.dp)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .height(46.dp)
                        .clickable {  }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Icon",
                        modifier = Modifier.padding(end = 8.dp)
                            .padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(text = "사진 추가", style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .height(46.dp)
                        .clickable {  }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Icon",
                        modifier = Modifier.padding(end = 8.dp)
                            .padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(text = "직접 추가", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}