package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.comp.shimmer

private const val SKELETON_ROW_COUNT = 5

/**
 * 레시피 목록 첫 로딩 중 표시하는 스켈레톤 (RecipeListItem 과 같은 배치)
 */
@Composable
fun RecipeListSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        repeat(SKELETON_ROW_COUNT) { RecipeRowSkeleton() }
    }
}

@Composable
private fun RecipeRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmer()
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Line(fraction = 0.6f, height = 16.dp)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Line(fraction = 1f, height = 12.dp)
                Line(fraction = 0.9f, height = 12.dp)
                Line(fraction = 0.7f, height = 12.dp)
            }
            Line(fraction = 0.4f, height = 12.dp)
        }
    }
}

@Composable
private fun Line(fraction: Float, height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .shimmer()
    )
}
