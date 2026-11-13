package com.yh.fridgesoksok.presentation.fridge.comp

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.common.comp.ErrorScreen
import com.yh.fridgesoksok.presentation.fridge.FridgeState
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.model.FoodModel

private const val STATE_CROSSFADE_MS = 450

@Composable
fun FoodListSection(
    mode: HomeUiMode,
    foods: List<FoodModel>,
    fridgeState: FridgeState,
    selectedFoods: Set<FoodModel> = emptySet(),
    onFoodSelectToggle: (FoodModel) -> Unit = {},
    onClickFood: (FoodModel) -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit,
    onRefreshClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.outline)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                modifier = Modifier.align(Alignment.TopCenter),
                painter = painterResource(id = R.drawable.lighting),
                contentScale = ContentScale.None,
                contentDescription = null,
            )

            // 스켈레톤 → 목록 전환이 툭 바뀌지 않도록 겹쳐서 서서히 교체
            Crossfade(
                targetState = fridgeState,
                animationSpec = tween(STATE_CROSSFADE_MS),
                label = "fridgeListState"
            ) { state ->
                when (state) {
                    FridgeState.Success -> {
                        FoodListContent(
                            mode = mode,
                            foods = foods,
                            selectedFoods = selectedFoods,
                            onFoodSelectToggle = onFoodSelectToggle,
                            onClickFood = onClickFood,
                            onClickMinus = onClickMinus,
                            onClickPlus = onClickPlus
                        )
                    }

                    // 첫 로딩은 카드 모양 스켈레톤으로 표시 (목록이 있으면 Loading 으로 바뀌지 않음)
                    FridgeState.Loading -> FoodListSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 28.dp, start = 16.dp, end = 16.dp)
                    )
                    FridgeState.Error -> ErrorScreen(onRefreshClick = onRefreshClick)
                }
            }
        }
    }
}

@Composable
private fun FoodListContent(
    mode: HomeUiMode,
    foods: List<FoodModel>,
    selectedFoods: Set<FoodModel>,
    onFoodSelectToggle: (FoodModel) -> Unit,
    onClickFood: (FoodModel) -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit
) {
    // Empty State 표시
    if (foods.isEmpty()) {
        FridgeEmptyState(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp)
        )
    } else {
        val gridState = rememberLazyGridState()
        // 맨 앞 항목이 바뀌면(영수증/직접 추가로 소비기한이 더 빠른 음식이 생기면) 맨 위로 이동
        // (key 기준으로 보던 위치를 유지하면 새 항목이 화면 위로 밀려 안 보임)
        var lastFirstId by remember { mutableStateOf(foods.first().id) }
        LaunchedEffect(foods.first().id) {
            val firstId = foods.first().id
            if (firstId != lastFirstId) {
                lastFirstId = firstId
                gridState.animateScrollToItem(0)
            }
        }

        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            items(
                items = foods,
                key = { it.id }
            ) { food ->
                FoodListItem(
                    // 추가/삭제/필터/정렬 변경 시 카드가 부드럽게 나타나고 자리를 옮김
                    modifier = Modifier.animateItem(),
                    mode = mode,
                    food = food,
                    isSelected = selectedFoods.contains(food),
                    onClick = {
                        if (mode == HomeUiMode.RECIPE_SELECT) onFoodSelectToggle(food)
                        else onClickFood(food)
                    },
                    onClickMinus = onClickMinus,
                    onClickPlus = onClickPlus,
                )
            }
        }
    }
}