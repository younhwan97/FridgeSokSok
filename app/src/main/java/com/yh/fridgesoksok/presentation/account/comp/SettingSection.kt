package com.yh.fridgesoksok.presentation.account.comp

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.comp.Divider

@Composable
fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    SettingSectionTitle(title)
    Divider(thickness = 2.dp)
    content()
    Spacer(modifier = Modifier.height(10.dp))
}