package com.yh.fridgesoksok.presentation.edit_food

import androidx.lifecycle.SavedStateHandle
import com.yh.fridgesoksok.data.repository.FakeFoodRepository
import com.yh.fridgesoksok.domain.usecase.SearchLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateFoodUseCase
import com.yh.fridgesoksok.presentation.model.FoodModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class EditFoodViewModelTest {

    private lateinit var viewModel: EditFoodViewModel
    private lateinit var fakeFoodRepository: FakeFoodRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        fakeFoodRepository = FakeFoodRepository()
        savedStateHandle = SavedStateHandle()
        viewModel = EditFoodViewModel(
            updateFoodUseCase = UpdateFoodUseCase(fakeFoodRepository),
            searchLocalFoodsUseCase = SearchLocalFoodsUseCase(fakeFoodRepository),
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `음식을 초기화하면 food가 설정되어야 한다`() {
        val food = FoodModel(
            id = "test-id",
            itemName = "사과",
            count = 1,
            expiryDate = "2025-12-31",
            categoryId = 1,
            fridgeId = "test-fridge",
            createdAt = "2025-01-01"
        )

        viewModel.initializeFood(food, isEditMode = true)

        assertNotNull(viewModel.food.value)
        assertEquals("사과", viewModel.food.value?.itemName)
        assertEquals(true, viewModel.isEditMode.value)
    }

    @Test
    fun `새 음식을 추가할 때는 기본값으로 초기화되어야 한다`() {
        viewModel.initializeFood(null, isEditMode = false)

        assertNotNull(viewModel.food.value)
        assertEquals(false, viewModel.isEditMode.value)
    }

    @Test
    fun `초기 상태는 Success여야 한다`() {
        assertEquals(EditFoodState.Success, viewModel.state.value)
    }
}
