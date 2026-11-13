package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yh.fridgesoksok.presentation.common.comp.ErrorScreen
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.recipe.RecipeState

private const val STATE_CROSSFADE_MS = 450

@Composable
fun RecipeListSection(
    recipeState: RecipeState,
    recipes: List<RecipeModel>,
    isSearching: Boolean,
    onClickItem: (RecipeModel) -> Unit,
    onDeleteClick: (RecipeModel) -> Unit,
    isBeingDeleted: (RecipeModel) -> Boolean,
    onDeleteAnimationEnd: (RecipeModel) -> Unit,
    onRefreshClick: () -> Unit
) {
    val listState = rememberLazyListState()
    var lastTopId by remember { mutableStateOf<String?>(null) }

    // 항목추가 감지
    LaunchedEffect(recipes.firstOrNull()?.id) {
        val newTopId = recipes.firstOrNull()?.id
        if (newTopId != null && newTopId != lastTopId) {
            lastTopId = newTopId
            listState.animateScrollToItem(0)
        }
    }

    // 스켈레톤 → 목록 전환이 툭 바뀌지 않도록 겹쳐서 서서히 교체
    Crossfade(
        targetState = recipeState,
        animationSpec = tween(STATE_CROSSFADE_MS),
        label = "recipeListState"
    ) { state ->
        when (state) {
            RecipeState.Success -> if (recipes.isEmpty()) {
                RecipeEmptyState(isSearching = isSearching)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(
                        items = recipes,
                        key = { it.id }
                    ) {
                        RecipeListItem(
                            item = it,
                            isBeingDeleted = isBeingDeleted(it),
                            onClick = { onClickItem(it) },
                            onDeleteClick = { onDeleteClick(it) },
                            onDeleteAnimationEnd = { onDeleteAnimationEnd(it) }
                        )
                    }
                }
            }

            RecipeState.Loading -> RecipeListSkeleton()
            RecipeState.Error -> ErrorScreen(onRefreshClick = onRefreshClick)
        }
    }
}