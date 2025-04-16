package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val capturedImage =
        navController.previousBackStackEntry?.savedStateHandle?.get<Bitmap>("capturedImage")

    if (capturedImage != null) {
        // imageBitmap을 활용하여 서버에 업로드
        viewModel.uploadReceiptImage(capturedImage)
    }
}