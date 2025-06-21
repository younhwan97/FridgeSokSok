package com.yh.fridgesoksok.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Divider(
    thickness: Dp = 1.dp
) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        thickness = thickness,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}