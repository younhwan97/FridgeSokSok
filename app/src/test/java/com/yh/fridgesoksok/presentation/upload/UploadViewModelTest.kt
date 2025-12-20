package com.yh.fridgesoksok.presentation.upload

import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.AddFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UploadReceiptImageUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UploadViewModelTest {

    private lateinit var viewModel: UploadViewModel
    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        fakeUserRepository = FakeUserRepository()

        // 테스트용 사용자 설정
        runBlocking {
            fakeUserRepository.saveUser(
                User(
                    id = "test-user",
                    accessToken = "test-token",
                    refreshToken = "test-refresh",
                    username = "Test User",
                    accountType = "KAKAO",
                    defaultFridgeId = "test-fridge"
                )
            ).first()
        }

        viewModel = UploadViewModel(
            loadUserUseCase = LoadUserUseCase(fakeUserRepository),
            addFoodsUseCase = AddFoodsUseCase(fakeFoodRepository),
            uploadReceiptImageUseCase = UploadReceiptImageUseCase(fakeFoodRepository)
        )
    }

    @Test
    fun `초기 상태는 Success여야 한다`() {
        assertEquals(UploadState.Success, viewModel.state.value)
    }

    @Test
    fun `초기 음식 목록은 비어있어야 한다`() {
        assertEquals(0, viewModel.newFoods.value.size)
    }

    @Test
    fun `음식을 추가하면 목록에 추가되어야 한다`() {
        val food = FoodModel(
            id = "",
            itemName = "사과",
            count = 1,
            expiryDate = "2025-12-31",
            categoryId = 1,
            fridgeId = "test-fridge",
            createdAt = "2025-01-01"
        )

        viewModel.addOrUpdateFood(food)

        assertEquals(1, viewModel.newFoods.value.size)
        assertEquals("사과", viewModel.newFoods.value.first().itemName)
    }
}
