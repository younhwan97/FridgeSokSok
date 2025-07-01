package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteFoodUseCaseTest {

    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var deleteFoodUseCase: DeleteFoodUseCase

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        deleteFoodUseCase = DeleteFoodUseCase(fakeFoodRepository)


    }

    @Test
    fun `foodId에 해당하는 음식이 삭제되어야 한다`() {
        assertTrue(false)
    }
}