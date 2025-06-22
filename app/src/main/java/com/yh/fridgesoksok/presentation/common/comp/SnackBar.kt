package com.yh.fridgesoksok.presentation.common.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Snackbar(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackBarHostState
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = Color(0xCC333333),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = it.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
    }
}