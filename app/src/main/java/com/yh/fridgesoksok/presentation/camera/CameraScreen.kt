package com.yh.fridgesoksok.presentation.camera

import androidx.activity.compose.BackHandler
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CameraScreen(
    navController: NavController,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight =
        with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }  // 실제 화면 높이(px)

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // offset Y 값 애니메이션
    val offsetY = remember { Animatable(screenHeight) }
    LaunchedEffect(Unit) {
        delay(100)
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    }

    val isExiting = remember { mutableStateOf(false) }

    LaunchedEffect(isExiting.value) {
        if (isExiting.value) {
            offsetY.animateTo(
                targetValue = screenHeight + 100,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
            navController.popBackStack()  // 애니메이션 끝난 뒤 pop
        }
    }

    BackHandler {
        onClose()
    }


    Column(
        modifier = Modifier.fillMaxSize()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .zIndex(3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .height(216.dp)
        ){

        }

        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .background(color = Color.Black)
                .height(144.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onClose()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.alpha(0f)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}