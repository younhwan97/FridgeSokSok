package com.yh.fridgesoksok.presentation.fridge

import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.CreateRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteFoodUseCase
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.Type
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FridgeViewModelTest {

    private lateinit var viewModel: FridgeViewModel
    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        fakeRecipeRepository = FakeRecipeRepository()
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

        viewModel = FridgeViewModel(
            getFoodsUseCase = GetFoodsUseCase(fakeFoodRepository),
            updateFoodUseCase = UpdateFoodUseCase(fakeFoodRepository),
            deleteFoodUseCase = DeleteFoodUseCase(fakeFoodRepository),
            loadUserUseCase = LoadUserUseCase(fakeUserRepository),
            createRecipeUseCase = CreateRecipeUseCase(fakeRecipeRepository)
        )
    }

    @Test
    fun `검색어 입력 시 typingQuery가 업데이트되어야 한다`() {
        viewModel.updateTypingQuery("사과")

        assertEquals("사과", viewModel.typingQuery.value)
    }

    @Test
    fun `검색어를 지우면 필터가 초기화되어야 한다`() {
        viewModel.updateFilterQuery("사과")
        viewModel.updateTypingQuery("")

        assertEquals("", viewModel.typingQuery.value)
    }

    @Test
    fun `음식 타입 필터가 업데이트되어야 한다`() {
        viewModel.updateSelectedType(Type.Meat)

        assertEquals(Type.Meat, viewModel.selectedType.value)
    }

    @Test
    fun `초기 상태는 Loading이어야 한다`() {
        assertEquals(FridgeState.Loading, viewModel.state.value)
    }

    @Test
    fun `초기 선택된 음식은 비어있어야 한다`() {
        assertEquals(0, viewModel.selectedFoods.value.size)
    }
}
