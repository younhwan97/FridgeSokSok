package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val newFoods by viewModel.newFoods.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val navBackStackEntryFlow = navController.currentBackStackEntryFlow

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        navBackStackEntryFlow.collectLatest { entry ->
            val result = entry.savedStateHandle.get<FoodModel>("newFood")
            result?.let {
                viewModel.addFood(it)
                entry.savedStateHandle.remove<FoodModel>("newFood")
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            UploadTopAppBar(
                onNavigationClick = { navController.popBackStack() },
                onActionClick = { navController.navigate(Screen.EditFoodScreen.route) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = listState
        ) {
            itemsIndexed(newFoods, key = { _, food -> food.id }) { index, food ->
                FoodBlock(
                    name = food.name,
                    type = food.type,
                    count = food.count,
                    endDt = food.endDt,
                    onDeleteClick = { viewModel.deleteFood(index) },
                    onIncreaseClick = { viewModel.increaseCount(index) },
                    onDecreaseClick = { viewModel.decreaseCount(index) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadTopAppBar(
    onNavigationClick: () -> Unit,
    onActionClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigationClick() },
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        },
        title = {
            Text(
                text = "식품 등록하기",
                style = MaterialTheme.typography.headlineMedium,
                color = CustomGreyColor7
            )
        },
        actions = {
            Button(
                modifier = Modifier.padding(end = 16.dp),
                onClick = { onActionClick() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "식품 추가",
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomGreyColor1
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun FoodBlock(
    name: String,
    type: Int,
    count: Int,
    endDt: String,
    onDeleteClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .widthIn(min = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CustomGreyColor2)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                text = Type.fromId(type).label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Image(
                modifier = Modifier.clickable { onDeleteClick() },
                painter = painterResource(R.drawable.close),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Type.fromId(type).icon),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomGreyColor7,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "소비기한 ${formatDate(endDt)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = CustomGreyColor5,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Row(
                        modifier = Modifier
                            .width(120.dp)
                            .height(30.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.clickable { onDecreaseClick() },
                            painter = painterResource(R.drawable.minus_primary),
                            contentDescription = null,
                            contentScale = ContentScale.None
                        )

                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomGreyColor7
                        )

                        Image(
                            modifier = Modifier.clickable { onIncreaseClick() },
                            painter = painterResource(R.drawable.plus_primary),
                            contentDescription = null,
                            contentScale = ContentScale.None
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "옵션변경",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val parsed = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"))
        parsed.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    } catch (e: Exception) {
        "" // 잘못된 포맷일 경우 빈 문자열 반환
    }
}