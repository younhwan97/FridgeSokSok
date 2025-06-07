package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yh.fridgesoksok.presentation.Screen

@Composable
fun HomeFabOverlay(
    currentRoute: String,
    isFabExpanded: Boolean,
    onClickFabOverlay: () -> Unit
){
    if (currentRoute == Screen.FridgeTab.route && isFabExpanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable { onClickFabOverlay() }
        )
    }
}