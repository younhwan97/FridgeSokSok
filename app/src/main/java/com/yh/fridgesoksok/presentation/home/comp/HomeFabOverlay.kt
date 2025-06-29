package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.presentation.Screen

@Composable
fun HomeFabOverlay(
    currentRoute: String,
    onClickFabOverlay: () -> Unit
){
    if (currentRoute == Screen.FridgeTab.route) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClickFabOverlay() }
        )
    }
}