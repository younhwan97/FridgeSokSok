package com.yh.fridgesoksok.presentation.upload

sealed class UploadState {
    data object Loading : UploadState()
    data object Success : UploadState()
    data object Error : UploadState()
}