package com.yh.fridgesoksok.presentation.home

import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
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
            getLocalApiUsageUseCase = GetLocalApiUsageUseCase(fakeUserRepository)
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

}
