package com.yh.fridgesoksok.presentation.home.fab

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.yh.fridgesoksok.R

@Composable
fun FloatingActionMenus(
    expanded: Boolean,
    fabOffset: Offset,
    screenWidth: Float,
    screenHeight: Float,
    menuWidthDp: Dp,
    menuHeightDp: Dp,
    onCaptureClick: () -> Unit,
    onUploadClick: () -> Unit,
    onManualClick: () -> Unit,
) {
    val menuWidth = with(LocalDensity.current) { menuWidthDp.toPx() }
    val menuHeight = with(LocalDensity.current) { menuHeightDp.toPx() }

    val context = LocalContext.current
    val cameraPermission = android.Manifest.permission.CAMERA
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한 허용 → 카메라 실행
                onCaptureClick()
            } else {
                // 권한 거부

            }
        }

    AnimatedVisibility(
        visible = expanded,
        enter = scaleIn(transformOrigin = TransformOrigin(0.5f, 1f)) + fadeIn(),
        exit = scaleOut(transformOrigin = TransformOrigin(0.5f, 1f)) + fadeOut(),
        modifier = Modifier
            .zIndex(2f)
            .absoluteOffset {
                val fabX = fabOffset.x
                val fabY = fabOffset.y

                // X 좌표 보정: 메뉴가 오른쪽으로 삐져나가는 경우 왼쪽으로 당김
                val offsetX = when {
                    fabX + menuWidth > screenWidth -> (screenWidth - menuWidth).toInt()
                    else -> (fabX - menuWidth + 100.dp.toPx()).toInt()
                }

                // Y 좌표 보정: FAB 위로 띄우되, 너무 위면 아래로 이동
                val offsetY = if (fabY - menuHeight > 0) {
                    (fabY - menuHeight).toInt()
                } else {
                    // 위에 공간 없으면 아래로
                    (fabY + 100.dp.toPx()).toInt()
                }

                IntOffset(offsetX - 32, offsetY - 32)
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .width(menuWidthDp)
                .height(menuHeightDp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            FabMenu(
                onClick = {
                    if (!hasRequiredPermissions(context = context))
                        cameraPermissionLauncher.launch(cameraPermission)
                    else
                        onCaptureClick()
                },
                iconResource = painterResource(R.drawable.camera),
                text = "영수증 찍기"
            )

            FabMenu(
                onClick = {
                },
                iconResource = painterResource(R.drawable.picture),
                text = "영수증 올리기"
            )

            FabMenu(
                onClick = {

                },
                iconResource = painterResource(R.drawable.pencil),
                text = "직접 추가하기"
            )
        }
    }
}

@Composable
fun FabMenu(
    onClick: () -> Unit,
    iconResource: Painter,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = iconResource,
            contentDescription = "Icon",
            modifier = Modifier
                .padding(end = 8.dp)
                .padding(start = 16.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }

}

private fun hasRequiredPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}