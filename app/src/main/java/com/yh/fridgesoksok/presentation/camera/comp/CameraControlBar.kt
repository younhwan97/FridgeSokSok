package com.yh.fridgesoksok.presentation.camera.comp

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraControlBar(
    capturedImage: Bitmap?,
    onCapture: () -> Unit,
    onRetake: () -> Unit,
    onUseImage: () -> Unit,
    onExit: () -> Unit,
    isExitEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(color = Color.Black)
            .height(144.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (capturedImage == null) {
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
                    onClick = onCapture,
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
                onClick = onExit,
                enabled = isExitEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "나가기",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        } else {
            TextButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "다시찍기",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onUseImage,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "사용하기",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Yellow
                )
            }
        }
    }
}