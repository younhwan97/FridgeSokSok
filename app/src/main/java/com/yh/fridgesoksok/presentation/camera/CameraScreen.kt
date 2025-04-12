package com.yh.fridgesoksok.presentation.camera

import android.app.Activity
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController

@Composable
fun CameraScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 네비게이션바 색상 설정
    val window = (context as Activity).window
    SideEffect { window.navigationBarColor = Color.Black.toArgb() }

    // 카메라 컨트롤
    val controller: LifecycleCameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    // 캡쳐 이미지
    val capturedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }

    // 뒤로가기 핸들링
    BackHandler(enabled = true) {
        if (capturedImageBitmap.value != null) {
            capturedImageBitmap.value = null
        } else {
            controller.unbind()
            navController.popBackStack()
        }
    }

    // 컴포저블 종료 시 리소스 정리
    DisposableEffect(Unit) {
        onDispose {
            controller.unbind()
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (capturedImageBitmap.value == null) {
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
                            this.controller = controller
                            controller.bindToLifecycle(lifecycleOwner)
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier.fillMaxSize()
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
                // 왼쪽 빈 공간
                Spacer(modifier = Modifier.weight(1f))

                // 촬영 버튼
                Column(
                    modifier = Modifier
                        .weight(1f)
                        ,
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

                            controller.takePicture(
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

                                        capturedImageBitmap.value = bitmap
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

                // 나가기 버튼
                TextButton(
                    onClick = {
                        controller.unbind()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
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
                    bitmap = capturedImageBitmap.value!!.asImageBitmap(),
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
                // 다시찍기 버튼
                TextButton(
                    onClick = { capturedImageBitmap.value = null },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "다시찍기",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 가운데 빈 공간
                Spacer(modifier = Modifier.weight(1f))

                // 사용하기 버튼
                TextButton(
                    onClick = {
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