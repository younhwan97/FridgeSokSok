package com.yh.fridgesoksok.presentation.login.comp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun BubbleText() {
    val bubbleWidth = 130.dp
    val bubbleHeight = 34.dp
    val cornerRadiusDp = 14.dp
    val tailWidthDp = 10.dp
    val tailHeightDp = 6.dp

    Box(
        modifier = Modifier.size(bubbleWidth, bubbleHeight),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val cornerRadius = cornerRadiusDp.toPx()
            val tailWidth = tailWidthDp.toPx()
            val tailHeight = tailHeightDp.toPx()
            val bodyHeight = size.height - tailHeight
            val doubleCorner = cornerRadius * 2

            val bubblePath = Path().apply {
                moveTo(cornerRadius, 0f)
                lineTo(size.width - cornerRadius, 0f)
                arcTo(
                    rect = Rect(
                        offset = Offset(size.width - doubleCorner, 0f),
                        size = Size(doubleCorner, doubleCorner)
                    ),
                    startAngleDegrees = -90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(size.width, bodyHeight - cornerRadius)
                arcTo(
                    rect = Rect(
                        offset = Offset(size.width - doubleCorner, bodyHeight - doubleCorner),
                        size = Size(doubleCorner, doubleCorner)
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(size.width / 2f + tailWidth / 2f, bodyHeight)
                lineTo(size.width / 2f, bodyHeight + tailHeight)
                lineTo(size.width / 2f - tailWidth / 2f, bodyHeight)
                lineTo(cornerRadius, bodyHeight)
                arcTo(
                    rect = Rect(
                        offset = Offset(0f, bodyHeight - doubleCorner),
                        size = Size(doubleCorner, doubleCorner)
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(0f, cornerRadius)
                arcTo(
                    rect = Rect(
                        offset = Offset(0f, 0f),
                        size = Size(doubleCorner, doubleCorner)
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                close()
            }

            drawPath(
                path = bubblePath,
                color = Color.White
            )

            drawPath(
                path = bubblePath,
                color = CustomGreyColor3,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Text(
            modifier = Modifier.offset(y = (-tailHeightDp / 2)),
            text = "3초만에 로그인하기",
            style = MaterialTheme.typography.bodySmall,
            color = CustomGreyColor7,
        )
    }
}
