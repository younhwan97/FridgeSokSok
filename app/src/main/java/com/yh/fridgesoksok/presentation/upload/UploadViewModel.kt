package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val uploadReceiptImageUseCase: UploadReceiptImageUseCase
) : ViewModel() {

    fun uploadReceiptImage(bitmap: Bitmap) {
        uploadReceiptImageUseCase(bitmap).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    //
                }

                is Resource.Error -> {
                    //
                }

                is Resource.Success -> {

                }
            }
        }.launchIn(viewModelScope)
    }
}