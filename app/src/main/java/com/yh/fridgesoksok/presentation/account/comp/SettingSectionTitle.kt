package com.yh.fridgesoksok.presentation.account.comp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun SettingSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}