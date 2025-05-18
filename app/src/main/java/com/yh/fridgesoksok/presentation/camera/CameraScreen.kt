package com.yh.fridgesoksok.presentation.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaActionSound
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.SharedViewModel

@Composable
fun CameraScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val systemUiController = rememberSystemUiController()

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
        }
    }

    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isExiting by remember { mutableStateOf(false) }
    var isScreenActive by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        systemUiController.setNavigationBarColor(Color.Black)
    }

    fun exitCameraScreen() {
        if (!isExiting) {
            isExiting = true
            isScreenActive = false
            navController.popBackStack()
            cameraController.unbind()
        }
    }

    BackHandler(enabled = true) {
        if (capturedImageBitmap != null) {
            capturedImageBitmap = null
        } else {
            exitCameraScreen()
        }
    }

    if (!isScreenActive) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (capturedImageBitmap == null) {
            Spacer(modifier = Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .weight(1f)
            ) {
                AndroidView(
                    factory = {
                        PreviewView(it).apply {
                            this.controller = cameraController
                            cameraController.bindToLifecycle(lifecycleOwner)
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = if (isScreenActive) 1f else 0f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(color = Color.Black)
                    .height(144.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "사진",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Yellow,
                    )

                    IconButton(
                        onClick = {
                            MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)

                            cameraController.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        super.onCaptureSuccess(image)

                                        val matrix = Matrix().apply {
                                            postRotate(image.imageInfo.rotationDegrees.toFloat())
                                        }

                                        val bitmap = Bitmap.createBitmap(
                                            image.toBitmap(),
                                            0, 0,
                                            image.width, image.height,
                                            matrix, true
                                        )

                                        capturedImageBitmap = bitmap
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("Camera", "Capture failed: ${exception.message}")
                                    }
                                }
                            )
                        },
                        modifier = Modifier.size(120.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                TextButton(
                    onClick = { exitCameraScreen() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "나가기",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(3f / 4f)
                    .weight(1f)
            ) {
                Image(
                    bitmap = capturedImageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(color = Color.Black)
                    .height(144.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { capturedImageBitmap = null },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "다시찍기",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        if (!isExiting) {
                            isExiting = true
                            sharedViewModel.setImage(capturedImageBitmap!!)
                            navController.navigate(Screen.UploadScreen.route)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "사용하기",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
