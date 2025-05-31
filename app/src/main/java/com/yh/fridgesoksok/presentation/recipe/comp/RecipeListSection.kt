package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.model.RecipeModel

@Composable
fun RecipeListSection(
    recipes: List<RecipeModel>,
    onClickItem: (RecipeModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recipes, key = { it.id }) { item ->
            RecipeListItem(
                item = item,
                onclick = { onClickItem(item) }
            )
        }
    }
}