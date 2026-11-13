package com.yh.fridgesoksok.presentation.fridge

import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.data.repository.FakeRecipeRepository
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.usecase.CreateRecipeFromFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.CreateRecipeUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteFoodUseCase
import com.yh.fridgesoksok.domain.usecase.GetFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_DELETE_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_LOAD_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.FOOD_UPDATE_FAILED
import com.yh.fridgesoksok.presentation.model.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FridgeViewModelTest {

    private lateinit var viewModel: FridgeViewModel
    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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
            createRecipeUseCase = CreateRecipeUseCase(fakeRecipeRepository),
            createRecipeFromFridgeUseCase = CreateRecipeFromFridgeUseCase(fakeRecipeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

    @Test
    fun `자동 레시피 생성 시 Loading 상태가 전달되어야 한다`() = runTest {
        // Given
        var capturedState: RecipeGenerationState? = null

        // When
        viewModel.generateRecipeFromFridge { state ->
            if (capturedState == null) {
                capturedState = state
            }
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(RecipeGenerationState.Loading, capturedState)
    }

    @Test
    fun `자동 레시피 생성 완료 후 Idle 상태가 전달되어야 한다`() = runTest {
        // Given
        var lastState: RecipeGenerationState? = null

        // When
        viewModel.generateRecipeFromFridge { state ->
            lastState = state
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(RecipeGenerationState.Idle, lastState)
    }

    @Test
    fun `목록 새로고침에 실패해도 기존 목록을 유지하고 알림을 보내야 한다`() = runTest {
        // Given
        val messages = collectErrorMessages()
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        fakeFoodRepository.shouldFail = true
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(listOf("food-1"), viewModel.foods.value.map { it.id })
        assertEquals(FridgeState.Success, viewModel.state.value)
        assertEquals(listOf(FOOD_LOAD_FAILED), messages)
    }

    @Test
    fun `첫 목록 로드에 실패하면 Error 상태가 되어야 한다`() = runTest {
        // Given
        fakeFoodRepository.shouldFail = true

        // When
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(FridgeState.Error, viewModel.state.value)
    }

    @Test
    fun `식품 삭제에 실패하면 목록을 유지하고 알림을 보내야 한다`() = runTest {
        // Given
        val messages = collectErrorMessages()
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()
        fakeFoodRepository.shouldFail = true

        // When
        viewModel.deleteFood(viewModel.foods.value.first())
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.foods.value.size)
        assertEquals(listOf(FOOD_DELETE_FAILED), messages)
    }

    @Test
    fun `식품 수정에 실패하면 알림을 보내야 한다`() = runTest {
        // Given
        val messages = collectErrorMessages()
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()
        fakeFoodRepository.shouldFail = true

        // When
        val target = viewModel.foods.value.first()
        viewModel.updateFood(target.copy(count = target.count + 1))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(target.count, viewModel.foods.value.first().count)
        assertEquals(listOf(FOOD_UPDATE_FAILED), messages)
    }

    @Test
    fun `수량 변경은 서버 응답 전에 화면에 바로 반영되어야 한다`() = runTest {
        // Given
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()
        val target = viewModel.foods.value.first()

        // When
        viewModel.updateFood(target.copy(count = target.count + 1))

        // Then (아직 서버 전송 전)
        assertEquals(target.count + 1, viewModel.foods.value.first().count)
        assertTrue(fakeFoodRepository.updatedFoods.isEmpty())
    }

    @Test
    fun `수량 연타는 마지막 값 한 번만 서버에 보내야 한다`() = runTest {
        // Given
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()
        val start = viewModel.foods.value.first().count

        // When: + 를 세 번 빠르게 누름 (화면 값 기준으로 다음 값을 계산)
        repeat(3) {
            val current = viewModel.foods.value.first()
            viewModel.updateFood(current.copy(count = current.count + 1))
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(start + 3, viewModel.foods.value.first().count)
        assertEquals(listOf(start + 3), fakeFoodRepository.updatedFoods.map { it.count })
    }

    @Test
    fun `전송 대기 중인 수량은 목록 새로고침에 덮어써지지 않아야 한다`() = runTest {
        // Given
        givenFoods(food("food-1", "우유"))
        viewModel.loadFoods()
        testDispatcher.scheduler.advanceUntilIdle()
        val target = viewModel.foods.value.first()
        viewModel.updateFood(target.copy(count = target.count + 2))

        // When: 디바운스 전에 새로고침
        viewModel.loadFoods()
        testDispatcher.scheduler.runCurrent()

        // Then
        assertEquals(target.count + 2, viewModel.foods.value.first().count)
    }

    private fun kotlinx.coroutines.test.TestScope.collectErrorMessages(): List<String> {
        val messages = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.errorMessage.collect { messages += it }
        }
        return messages
    }

    private fun givenFoods(vararg foods: Food) = runBlocking {
        fakeFoodRepository.addFoods("test-fridge", foods.toList()).first()
    }

    private fun food(id: String, name: String) = Food(
        id = id,
        fridgeId = "test-fridge",
        itemName = name,
        expiryDate = "20301231",
        categoryId = 1,
        count = 1,
        createdAt = "20250101"
    )
}
