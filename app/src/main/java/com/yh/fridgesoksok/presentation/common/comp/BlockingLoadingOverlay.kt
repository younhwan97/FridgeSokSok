package com.yh.fridgesoksok.presentation.common.comp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex

@Composable
fun BlockingLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}