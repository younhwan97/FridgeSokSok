package com.yh.fridgesoksok.presentation.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaActionSound
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.camera.comp.CameraControlBar
import com.yh.fridgesoksok.presentation.camera.comp.CameraPreviewView
import com.yh.fridgesoksok.presentation.camera.comp.CapturedImageView
import com.yh.fridgesoksok.presentation.common.util.rememberBackPressCooldown
import com.yh.fridgesoksok.presentation.common.util.rememberCameraController

@Composable
fun CameraScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val systemUiController = rememberSystemUiController()
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val (isBackEnabled, triggerBackCooldown) = rememberBackPressCooldown()
    val cameraController = rememberCameraController(context)

    LaunchedEffect(true) {
        systemUiController.setNavigationBarColor(Color.Black)
    }

    BackHandler(enabled = isBackEnabled) {
        if (capturedImageBitmap != null) {
            capturedImageBitmap = null
        } else {
            triggerBackCooldown()
            exitCamera(navController, cameraController)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.height(72.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .weight(1f)
        ) {
            capturedImageBitmap?.let { bitmap ->
                CapturedImageView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    bitmap = bitmap
                )
            } ?: CameraPreviewView(
                cameraController = cameraController,
                lifecycleOwner = lifecycleOwner
            )
        }

        CameraControlBar(
            capturedImage = capturedImageBitmap,
            onCapture = {
                MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
                captureImage(
                    controller = cameraController,
                    context = context,
                    onSuccess = { capturedImageBitmap = it },
                    onError = { Log.e("Camera", "Capture failed: ${it.message}") }
                )
            },
            onRetake = { capturedImageBitmap = null },
            onUseImage = {
                capturedImageBitmap?.let {
                    triggerBackCooldown()
                    sharedViewModel.setReceipt(it)
                    navController.navigate(Screen.UploadScreen.route)
                }
            },
            onExit = {
                triggerBackCooldown()
                exitCamera(navController, cameraController)
            },
            isExitEnabled = isBackEnabled
        )
    }
}

private fun exitCamera(navController: NavController, cameraController: LifecycleCameraController) {
    cameraController.unbind()
    navController.popBackStack()
}

private fun captureImage(
    controller: LifecycleCameraController,
    context: android.content.Context,
    onSuccess: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                onSuccess(
                    Bitmap.createBitmap(
                        image.toBitmap(),
                        0, 0,
                        image.width, image.height,
                        matrix, true
                    )
                )
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}