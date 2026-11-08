package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.model.Receipt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UploadReceiptImageUseCaseTest {

    private lateinit var uploadReceiptImage: UploadReceiptImageUseCase
    private lateinit var fakeFoodRepository: FakeFoodRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        uploadReceiptImage = UploadReceiptImageUseCase(fakeFoodRepository)
    }

    @Test
    fun `영수증 이미지를 업로드하고 결과를 받아야 한다`() = runBlocking {
        // Given
        val image = byteArrayOf(1, 2, 3)
        val receipts = listOf(
            Receipt(
                id = 1,
                itemName = "우유",
                expirationDescription = "냉장 보관",
                foodType = "유제품",
                shelfLifeHours = 168,
                categoryId = 1,
                count = 1
            )
        )
        fakeFoodRepository.setReceipts(receipts)

        // When
        val result = uploadReceiptImage(image).first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(receipts, (result as Resource.Success).data)
        assertArrayEquals(image, fakeFoodRepository.lastUploadedImage)
    }
}
