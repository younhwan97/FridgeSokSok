package com.yh.fridgesoksok.presentation.recipe_detail.comp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun RecipeContent(
    recipeContent: String
) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = recipeContent,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
        fontWeight = FontWeight.Normal,
        color = CustomGreyColor7
    )
}