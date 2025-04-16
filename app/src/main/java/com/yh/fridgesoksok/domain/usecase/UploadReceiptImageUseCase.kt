package com.yh.fridgesoksok.domain.usecase

import android.graphics.Bitmap
import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class UploadReceiptImageUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {

    operator fun invoke(img: Bitmap) =
        foodRepository.uploadReceiptImage(img = img)
}