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
import org.junit.Assert.assertFalse
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

    @Test
    fun `편집 화면을 거친 항목은 소비기한 입력 필요 표시가 해제되어야 한다`() {
        // Given: 미매칭 항목이 목록에 있음
        val unmatched = FoodModel(
            id = "receipt-1",
            itemName = "삼다수",
            count = 2,
            expiryDate = "20261108",
            categoryId = 1,
            fridgeId = "",
            createdAt = "20261108",
            needsExpiryInput = true
        )
        viewModel.addOrUpdateFood(unmatched)

        // When: 편집 화면에서 날짜를 정하고 돌아옴
        viewModel.addOrUpdateFood(unmatched.copy(expiryDate = "20271108"))

        // Then
        val food = viewModel.newFoods.value.single()
        assertEquals("20271108", food.expiryDate)
        assertFalse(food.needsExpiryInput)
    }

    @Test
    fun `편집 화면을 거친 항목은 포장 확인 표시도 해제되어야 한다`() {
        val labelFood = FoodModel(
            id = "receipt-2",
            itemName = "서울우유",
            count = 1,
            expiryDate = "20270328",
            categoryId = 6,
            fridgeId = "",
            createdAt = "20261108",
            needsLabelCheck = true
        )
        viewModel.addOrUpdateFood(labelFood)

        viewModel.addOrUpdateFood(labelFood.copy(expiryDate = "20261120"))

        assertFalse(viewModel.newFoods.value.single().needsLabelCheck)
    }
}
