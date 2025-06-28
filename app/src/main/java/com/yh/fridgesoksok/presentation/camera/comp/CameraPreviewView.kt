package com.yh.fridgesoksok.presentation.camera.comp

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreviewView(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraController: LifecycleCameraController,
    modifier: Modifier = Modifier,
    onCameraReady: (() -> Unit)? = null
) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
                scaleType = PreviewView.ScaleType.FILL_CENTER

                previewStreamState.observe(lifecycleOwner) { state ->
                    if (state == PreviewView.StreamState.STREAMING) {
                        onCameraReady?.invoke()
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}