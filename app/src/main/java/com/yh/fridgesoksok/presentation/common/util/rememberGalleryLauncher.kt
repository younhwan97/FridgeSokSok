package com.yh.fridgesoksok.presentation.common.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * 시스템 사진 선택기로 이미지 1장 선택
 * 사진 선택기(미지원 기기는 문서 선택기로 대체)는 저장소 권한이 필요 없으므로 권한을 요청하지 않음
 */
@Composable
fun rememberGalleryPickerLauncher(
    onImageSelected: (Uri) -> Unit = {}
): () -> Unit {
    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onImageSelected) }

    return {
        pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
