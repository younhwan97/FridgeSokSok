package com.yh.fridgesoksok.presentation.upload.comp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import kotlinx.coroutines.delay

// 서버 처리 순서(OCR → 품목 정리 → 소비기한 매칭)에 맞춘 안내 문구와 다음 문구까지의 시간
private val ANALYZING_STEPS = listOf(
    "영수증을 읽고 있어요" to 2500L,
    "품목을 정리하고 있어요" to 4000L,
    "소비기한을 찾고 있어요" to 5000L,
    "거의 다 됐어요!" to Long.MAX_VALUE
)

/**
 * 영수증 인식 대기 화면 (수 초 걸리므로 진행 중임을 문구로 안내)
 */
@Composable
fun UploadAnalyzingReceipt() {
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (step < ANALYZING_STEPS.lastIndex) {
            delay(ANALYZING_STEPS[step].second)
            step++
        }
    }

    BlockingLoadingOverlay(
        bgColor = Color.Black.copy(alpha = 0.66f),
        showLoading = false,
        showContent = true
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = CustomGreyColor1
            )

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInVertically(tween(300)) { it / 2 } + fadeIn(tween(300)))
                        .togetherWith(slideOutVertically(tween(300)) { -it / 2 } + fadeOut(tween(200)))
                },
                label = "analyzingStep"
            ) { index ->
                Text(
                    text = ANALYZING_STEPS[index].first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomGreyColor1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
