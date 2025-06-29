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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel
import com.yh.fridgesoksok.presentation.camera.comp.*
import com.yh.fridgesoksok.presentation.common.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val cameraController = rememberCameraController(context)
    var isCameraReady by remember { mutableStateOf(false) }
    var isPreviewVisible by remember { mutableStateOf(true) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown(delayMillis = 1400)

    // 카메라 해제
    DisposableEffect(Unit) {
        onDispose { cameraController.unbind() }
    }

    // 뒤로가기 처리
    BackHandler(enabled = canTrigger) {
        if (capturedImageBitmap != null) {
            capturedImageBitmap = null
        } else {
            exitCamera(
                scope = coroutineScope,
                controller = cameraController,
                navController = navController,
                triggerCooldown = triggerCooldown,
                onPreviewDetach = { isPreviewVisible = false }
            )
        }
    }

    // 화면
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
            when {
                capturedImageBitmap != null -> {
                    CapturedImageView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        previewImage = capturedImageBitmap!!
                    )
                }

                else -> {
                    if (isPreviewVisible) {
                        CameraPreviewView(
                            lifecycleOwner = lifecycleOwner,
                            cameraController = cameraController,
                            onCameraReady = { isCameraReady = true }
                        )
                    }

                    if (!isCameraReady) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        CameraControlBar(
            capturedImage = capturedImageBitmap,
            isExitEnabled = canTrigger,
            onCapture = {
                MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
                captureImage(
                    context = context,
                    controller = cameraController,
                    onSuccess = { capturedImageBitmap = it },
                    onError = { Log.e("Camera", "Capture failed: ${it.message}") }
                )
            },
            onRetake = { capturedImageBitmap = null },
            onUseImage = {
                capturedImageBitmap?.let {
                    triggerCooldown()
                    sharedViewModel.setReceipt(it)
                    navController.navigate(Screen.UploadScreen.route)
                }
            },
            onExit = {
                exitCamera(
                    scope = coroutineScope,
                    controller = cameraController,
                    navController = navController,
                    triggerCooldown = triggerCooldown,
                    onPreviewDetach = { isPreviewVisible = false }
                )
            }
        )
    }
}

private fun captureImage(
    context: android.content.Context,
    controller: LifecycleCameraController,
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

private fun exitCamera(
    scope: CoroutineScope,
    controller: LifecycleCameraController,
    navController: NavController,
    triggerCooldown: () -> Unit,
    onPreviewDetach: () -> Unit
) {
    onPreviewDetach()
    triggerCooldown()
    controller.unbind()
    scope.launch {
        delay(100) // 잔상 제거
        navController.popBackStack()
    }
}