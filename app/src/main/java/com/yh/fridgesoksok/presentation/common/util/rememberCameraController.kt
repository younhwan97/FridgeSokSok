package com.yh.fridgesoksok.presentation.common.util

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberCameraController(context: android.content.Context): LifecycleCameraController {
    return remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
        }
    }
}

