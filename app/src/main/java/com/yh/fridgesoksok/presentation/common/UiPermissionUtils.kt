package com.yh.fridgesoksok.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yh.fridgesoksok.presentation.common.extension.isNotificationPermissionGranted

@Composable
fun rememberNotificationPermissionState(): State<Boolean> {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(context.isNotificationPermissionGranted()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            permissionGranted = context.isNotificationPermissionGranted()
        }
    }

    return rememberUpdatedState(permissionGranted)
}