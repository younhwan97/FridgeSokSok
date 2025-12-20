package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * UploadReceiptImageUseCase 테스트
 *
 * 참고: 이 테스트는 Android Bitmap을 사용하므로 유닛 테스트에서 실행할 수 없습니다.
 * Instrumented 테스트 또는 Robolectric을 사용한 테스트가 필요합니다.
 */
@Ignore("Bitmap requires Android framework - use instrumented test instead")
class UploadReceiptImageUseCaseTest {

    private lateinit var uploadReceiptImage: UploadReceiptImageUseCase
    private lateinit var fakeFoodRepository: FakeFoodRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        uploadReceiptImage = UploadReceiptImageUseCase(fakeFoodRepository)
    }

    @Test
    fun `영수증 이미지를 업로드하고 결과를 받아야 한다`() {
        // Android Bitmap이 필요하므로 instrumented test로 이동 필요
    }
}
