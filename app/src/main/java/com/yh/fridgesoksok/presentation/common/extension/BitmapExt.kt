package com.yh.fridgesoksok.presentation.common.extension

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * 최대 크기에 맞게 축소(확대는 하지 않음)한 뒤 JPEG 바이트로 변환
 * 원본 Bitmap은 recycle 하지 않는다.
 */
fun Bitmap.toResizedJpegBytes(
    maxWidth: Int = 1080,
    maxHeight: Int = 1080,
    quality: Int = 100
): ByteArray {
    val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height, 1f)
    val resized = if (ratio < 1f) {
        Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
    } else {
        this
    }

    return try {
        ByteArrayOutputStream().use { outputStream ->
            resized.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        }
    } finally {
        if (resized !== this) resized.recycle()
    }
}
