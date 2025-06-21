package com.yh.fridgesoksok.presentation.common

import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation

class ResizeTransformation(
    private val targetWidth: Int,
    private val targetHeight: Int
) : Transformation {

    override val cacheKey: String
        get() = "ResizeTransformation($targetWidth,$targetHeight)"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Bitmap.createScaledBitmap(input, targetWidth, targetHeight, true)
    }
}