package com.yh.fridgesoksok.presentation.camera.comp

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun CapturedImageView(
    modifier: Modifier = Modifier,
    previewImage: Bitmap
) {
    Image(
        modifier = modifier,
        bitmap = previewImage.asImageBitmap(),
        contentDescription = null,
    )
}