package com.yh.fridgesoksok.presentation.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun rememberBackPressCooldown(delayMillis: Long = 500): Pair<Boolean, () -> Unit> {
    var isEnabled by remember { mutableStateOf(true) }
    var cooldownTrigger by remember { mutableStateOf(false) }

    if (cooldownTrigger) {
        LaunchedEffect(Unit) {
            isEnabled = false
            delay(delayMillis)
            isEnabled = true
            cooldownTrigger = false
        }
    }

    return isEnabled to { cooldownTrigger = true }
}