package com.yh.fridgesoksok.presentation.home

import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.presentation.fridge.RecipeGenerationState
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeUserRepository: FakeUserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        viewModel = HomeViewModel(
            getLocalApiUsageUseCase = GetLocalApiUsageUseCase(fakeUserRepository),
            getApiUsageUseCase = GetApiUsageUseCase(fakeUserRepository),
            updateApiUsageUseCase = UpdateApiUsageUseCase(fakeUserRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 UI 모드는 DEFAULT여야 한다`() {
        assertEquals(HomeUiMode.DEFAULT, viewModel.uiMode.value)
    }

    @Test
    fun `레시피 모드로 변경하면 RECIPE_SELECT가 되어야 한다`() {
        viewModel.updateToRecipeUiMode()

        assertEquals(HomeUiMode.RECIPE_SELECT, viewModel.uiMode.value)
    }

    @Test
    fun `기본 모드로 변경하면 DEFAULT가 되어야 한다`() {
        viewModel.updateToRecipeUiMode()
        viewModel.updateToDefaultUiMode()

        assertEquals(HomeUiMode.DEFAULT, viewModel.uiMode.value)
    }

    @Test
    fun `UI 모드를 리셋하면 DEFAULT가 되어야 한다`() {
        viewModel.updateToRecipeUiMode()
        viewModel.resetUiMode()

        assertEquals(HomeUiMode.DEFAULT, viewModel.uiMode.value)
    }

    @Test
    fun `자동 레시피 생성 트리거가 발생해야 한다`() = runTest {
        // Given
        var triggerCount = 0
        val job = launch {
            viewModel.generateRecipeFromFridgeTrigger.collect {
                triggerCount++
            }
        }

        // When
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.triggerGenerateRecipeFromFridge()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, triggerCount)
        job.cancel()
    }

    @Test
    fun `레시피 생성 트리거가 발생해야 한다`() = runTest {
        // Given
        var triggerCount = 0
        val job = launch {
            viewModel.generateRecipeTrigger.collect {
                triggerCount++
            }
        }

        // When
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.triggerGenerateRecipe()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, triggerCount)
        job.cancel()
    }


    @Test
    fun `레시피 생성이 끝나면 서버 사용량으로 생성 가능 여부를 갱신해야 한다`() = runTest {
        // Given: 초기에는 사용 가능
        assertTrue(viewModel.isRecipeGenerationAvailable.value)
        fakeUserRepository.updateApiUsage(
            listOf(ApiUsage(feature = "RECIPE_GENERATION", max = 3, used = 3))
        ).first()

        // When: 레시피 생성 Loading -> Idle
        viewModel.updateRecipeGenerationState(RecipeGenerationState.Loading)
        viewModel.updateRecipeGenerationState(RecipeGenerationState.Idle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.isRecipeGenerationAvailable.value)
    }

    @Test
    fun `레시피 생성 API 호출 없이 Idle로 바뀌면 사용량을 갱신하지 않아야 한다`() = runTest {
        // Given
        fakeUserRepository.updateApiUsage(
            listOf(ApiUsage(feature = "RECIPE_GENERATION", max = 3, used = 3))
        ).first()

        // When: Loading 을 거치지 않음
        viewModel.updateRecipeGenerationState(RecipeGenerationState.Idle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: 초기 값 유지
        assertTrue(viewModel.isRecipeGenerationAvailable.value)
    }

    @Test
    fun `서버 사용량으로 남은 레시피·영수증 횟수를 갱신해야 한다`() = runTest {
        // Given
        fakeUserRepository.updateApiUsage(
            listOf(
                ApiUsage(feature = "RECIPE_GENERATION", max = 3, used = 2),
                ApiUsage(feature = "RECEIPT_OCR", max = 2, used = 2)
            )
        ).first()

        // When
        viewModel.refreshApiUsage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.recipeRemaining.value)
        assertEquals(0, viewModel.receiptRemaining.value)
        assertTrue(viewModel.isRecipeGenerationAvailable.value)
    }

    @Test
    fun `한도 정보가 없으면 남은 횟수를 모르는 것으로 둔다`() = runTest {
        // Given: 영수증 한도 정보 없음 (max 0)
        fakeUserRepository.updateApiUsage(
            listOf(ApiUsage(feature = "RECEIPT_OCR", max = 0, used = 0))
        ).first()

        // When
        viewModel.refreshApiUsage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: 0 으로 보고 메뉴를 막지 않도록 null
        assertEquals(null, viewModel.receiptRemaining.value)
    }
}
