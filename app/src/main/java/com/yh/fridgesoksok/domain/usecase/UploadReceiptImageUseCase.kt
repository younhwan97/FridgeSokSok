package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class UploadReceiptImageUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    /**
     * @param image JPEG 인코딩된 영수증 이미지
     */
    operator fun invoke(image: ByteArray) =
        foodRepository.uploadReceiptImage(image = image)
}