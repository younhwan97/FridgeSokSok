package com.yh.fridgesoksok.presentation.home

import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetLocalApiUsageUseCase
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        viewModel = HomeViewModel(
            getLocalApiUsageUseCase = GetLocalApiUsageUseCase(fakeUserRepository)
        )
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

}
