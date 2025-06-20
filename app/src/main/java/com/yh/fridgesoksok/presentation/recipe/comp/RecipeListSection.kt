package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.recipe.RecipeState

@Composable
fun RecipeListSection(
    recipeState: RecipeState,
    recipes: List<RecipeModel>,
    onClickItem: (RecipeModel) -> Unit,
    onDeleteClick: (RecipeModel) -> Unit,
    isBeingDeleted: (RecipeModel) -> Boolean,
    onDeleteAnimationEnd: (RecipeModel) -> Unit
) {
    // 로딩 상태 표시
    AnimatedVisibility(
        visible = recipeState != RecipeState.Success,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    // 성공 시 리스트 표시
    if (recipeState == RecipeState.Success) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = recipes,
                key = { it.id }
            ) { item ->
                RecipeListItem(
                    item = item,
                    isBeingDeleted = isBeingDeleted(item),
                    onClick = { onClickItem(item) },
                    onDeleteClick = { onDeleteClick(item) },
                    onDeleteAnimationEnd = { onDeleteAnimationEnd(item) }
                )
            }
        }
    }
}