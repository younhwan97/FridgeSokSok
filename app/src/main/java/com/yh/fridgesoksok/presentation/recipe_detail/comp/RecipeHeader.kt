package com.yh.fridgesoksok.presentation.recipe_detail.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.common.extension.toFormattedDate
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun RecipeHeader(
    recipeName: String,
    recipeCreatedAt: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = recipeName,
            style = MaterialTheme.typography.bodyLarge,
            color = CustomGreyColor7
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            // 서버 값(2026-11-13T20:19:04.99) 대신 목록과 같은 형식(2026.11.13)으로 표시
            text = "생성날짜 ${remember(recipeCreatedAt) { recipeCreatedAt.toFormattedDate(DateFormatter.yyyyMMddDot) }}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = CustomGreyColor5
        )
    }
}