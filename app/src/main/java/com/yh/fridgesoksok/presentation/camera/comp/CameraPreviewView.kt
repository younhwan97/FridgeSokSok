package com.yh.fridgesoksok.presentation.camera.comp

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreviewView(
    cameraController: LifecycleCameraController,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}