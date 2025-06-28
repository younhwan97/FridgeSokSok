package com.yh.fridgesoksok.presentation.home.comp

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R

@Composable
fun FloatingActionMenus(
    expanded: Boolean,
    onCaptureClick: () -> Unit,
    onUploadClick: () -> Unit,
    onManualClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = expanded,
        enter = scaleIn(transformOrigin = TransformOrigin(0.5f, 1f)) + fadeIn(),
        exit = scaleOut(transformOrigin = TransformOrigin(0.5f, 1f)) + fadeOut(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .width(186.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            FabMenu(
                onClick = { onCaptureClick() },
                iconResource = painterResource(R.drawable.camera),
                text = "영수증 찍기"
            )

            FabMenu(
                onClick = { onUploadClick() },
                iconResource = painterResource(R.drawable.picture),
                text = "영수증 올리기"
            )

            FabMenu(
                onClick = { onManualClick() },
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

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}