package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.home.HomeUiMode

@Composable
fun HomeFabBar(
    mode: HomeUiMode,
    currentRoute: String,
    isFabMenuExpanded: Boolean,
    onToggleFab: () -> Unit,
    onCaptureClick: () -> Unit,
    onUploadClick: () -> Unit,
    onManualClick: () -> Unit,
) {
    if (currentRoute == Screen.FridgeTab.route && mode == HomeUiMode.DEFAULT) {
        Column(
            modifier = Modifier.zIndex(99f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionMenus(
                expanded = isFabMenuExpanded,
                onCaptureClick = onCaptureClick,
                onUploadClick = onUploadClick,
                onManualClick = onManualClick
            )

            FloatingActionButton(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.End),
                expanded = isFabMenuExpanded,
                onClick = onToggleFab
            )
        }
    }
}