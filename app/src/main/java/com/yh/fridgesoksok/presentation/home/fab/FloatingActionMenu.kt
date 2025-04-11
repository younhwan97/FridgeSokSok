package com.yh.fridgesoksok.presentation.home.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.R

@Composable
fun FloatingActionMenu(
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

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable {
                        onCaptureClick()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .padding(start = 16.dp)
                        .size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = "영수증 찍기", style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable { onUploadClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.picture),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .padding(start = 16.dp)
                        .size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = "영수증 올리기", style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable { onManualClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pencil),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .padding(start = 16.dp)
                        .size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = "직접 추가하기", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}