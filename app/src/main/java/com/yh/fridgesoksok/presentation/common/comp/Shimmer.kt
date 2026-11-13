package com.yh.fridgesoksok.presentation.common.comp

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3

private const val SHIMMER_DURATION_MS = 1100
private const val SHIMMER_WIDTH = 600f

/**
 * 로딩 중인 영역을 회색 빛이 지나가는 형태로 표시 (스켈레톤/이미지 placeholder 공용)
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -SHIMMER_WIDTH,
        targetValue = SHIMMER_WIDTH * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    background(
        Brush.linearGradient(
            colors = listOf(CustomGreyColor3, CustomGreyColor2, CustomGreyColor3),
            start = Offset(offset, offset),
            end = Offset(offset + SHIMMER_WIDTH, offset + SHIMMER_WIDTH)
        )
    )
}
