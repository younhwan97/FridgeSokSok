package com.yh.fridgesoksok.presentation.login.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun LoginCenterContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        SubcomposeLayout { constraints ->
            val textPlaceable = subcompose("text") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "냉장고 속속에\n오신 것을 환영합니다!",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = CustomGreyColor7
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "3초 가입으로 지금바로 시작해보세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CustomGreyColor5
                    )
                }
            }.map { it.measure(constraints) }

            val textHeight = textPlaceable.sumOf { it.height }
            val textWidth = textPlaceable.maxOf { it.width }

            val imagePlaceable = subcompose("image") {
                Image(
                    painter = painterResource(R.drawable.fridge),
                    contentDescription = null,
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }.map { it.measure(constraints) }.first()

            val centerY = constraints.maxHeight / 2
            val textY = centerY - textHeight / 2
            val imageBottomY = textY - 16.dp.roundToPx()
            val imageY = imageBottomY - imagePlaceable.height

            layout(constraints.maxWidth, constraints.maxHeight) {
                val textX = (constraints.maxWidth - textWidth) / 2
                var currentY = textY
                textPlaceable.forEach {
                    it.place(textX, currentY)
                    currentY += it.height
                }

                val imageX = (constraints.maxWidth - imagePlaceable.width) / 2
                imagePlaceable.place(imageX, imageY)
            }
        }
    }
}