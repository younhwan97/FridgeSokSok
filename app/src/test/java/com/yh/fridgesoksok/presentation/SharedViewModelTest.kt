package com.yh.fridgesoksok.presentation

import com.yh.fridgesoksok.common.SessionEvents
import com.yh.fridgesoksok.presentation.model.RecipeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * SharedViewModel 테스트
 *
 * 참고: Bitmap은 Android framework가 필요하므로 receipt 관련 테스트는 제외합니다.
 */
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel

    @Before
    fun setUp() {
        viewModel = SharedViewModel(SessionEvents())
    }

    @Test
    fun `receipt를 초기화하면 null이 되어야 한다`() {
        viewModel.clearReceipt()

        assertNull(viewModel.receipt.value)
    }

    @Test
    fun `선택된 레시피를 설정하면 값이 업데이트되어야 한다`() {
        val recipe = RecipeModel(
            id = "test-id",
            recipeImageUrl = "https://example.com/image.jpg",
            recipeName = "테스트 레시피",
            recipeContent = "테스트 내용",
            ingredients = listOf("재료1", "재료2"),
            ingredientTypes = listOf(1, 2),
            createdAt = "2025-01-01"
        )

        viewModel.setSelectedRecipe(recipe)

        assertNotNull(viewModel.selectedRecipe.value)
        assertEquals("테스트 레시피", viewModel.selectedRecipe.value?.recipeName)
    }

    @Test
    fun `선택된 레시피를 초기화하면 null이 되어야 한다`() {
        viewModel.clearSelectedRecipe()

        assertNull(viewModel.selectedRecipe.value)
    }
}
