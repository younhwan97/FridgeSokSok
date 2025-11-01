package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.model.UserSetting
import com.yh.fridgesoksok.presentation.model.toPresentation
import org.junit.Assert.assertEquals
import org.junit.Test

class MappingTest {

    @Test
    fun `FoodEntity를 Domain Food로 변환해야 한다`() {
        // Given
        val entity = FoodEntity(
            id = "food1",
            fridgeId = "fridge1",
            itemName = "사과",
            expiryDate = "2025-12-31",
            categoryId = 1,
            count = 5,
            createdAt = "2025-01-01"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("food1", domain.id)
        assertEquals("fridge1", domain.fridgeId)
        assertEquals("사과", domain.itemName)
        assertEquals("2025-12-31", domain.expiryDate)
        assertEquals(1, domain.categoryId)
        assertEquals(5, domain.count)
        assertEquals("2025-01-01", domain.createdAt)
    }

    @Test
    fun `Domain Food를 FoodEntity로 변환해야 한다`() {
        // Given
        val domain = Food(
            id = "food2",
            fridgeId = "fridge2",
            itemName = "바나나",
            expiryDate = "2025-11-30",
            categoryId = 2,
            count = 3,
            createdAt = "2025-01-02"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("food2", entity.id)
        assertEquals("fridge2", entity.fridgeId)
        assertEquals("바나나", entity.itemName)
        assertEquals("2025-11-30", entity.expiryDate)
        assertEquals(2, entity.categoryId)
        assertEquals(3, entity.count)
        assertEquals("2025-01-02", entity.createdAt)
    }

    @Test
    fun `FoodEntity와 Food 간 양방향 변환이 동일한 데이터를 유지해야 한다`() {
        // Given
        val originalEntity = FoodEntity(
            id = "food3",
            fridgeId = "fridge3",
            itemName = "오렌지",
            expiryDate = "2025-10-31",
            categoryId = 3,
            count = 10,
            createdAt = "2025-01-03"
        )

        // When
        val domain = originalEntity.toDomain()
        val convertedEntity = domain.toEntity()

        // Then
        assertEquals(originalEntity, convertedEntity)
    }

    @Test
    fun `RecipeEntity를 Domain Recipe로 변환해야 한다`() {
        // Given
        val entity = RecipeEntity(
            id = "recipe1",
            recipeImageUrl = "https://example.com/recipe.jpg",
            recipeName = "사과 샐러드",
            recipeContent = "맛있는 레시피",
            ingredients = listOf("사과", "양상추"),
            ingredientTypes = listOf(1, 2),
            createdAt = "2025-01-01"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("recipe1", domain.id)
        assertEquals("https://example.com/recipe.jpg", domain.recipeImageUrl)
        assertEquals("사과 샐러드", domain.recipeName)
        assertEquals("맛있는 레시피", domain.recipeContent)
        assertEquals(listOf("사과", "양상추"), domain.ingredients)
        assertEquals(listOf(1, 2), domain.ingredientTypes)
        assertEquals("2025-01-01", domain.createdAt)
    }

    @Test
    fun `Domain Recipe를 RecipeEntity로 변환해야 한다`() {
        // Given
        val domain = Recipe(
            id = "recipe2",
            recipeImageUrl = "https://example.com/recipe2.jpg",
            recipeName = "바나나 스무디",
            recipeContent = "시원한 레시피",
            ingredients = listOf("바나나", "우유"),
            ingredientTypes = listOf(2, 3),
            createdAt = "2025-01-02"
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals("recipe2", entity.id)
        assertEquals("https://example.com/recipe2.jpg", entity.recipeImageUrl)
        assertEquals("바나나 스무디", entity.recipeName)
        assertEquals("시원한 레시피", entity.recipeContent)
        assertEquals(listOf("바나나", "우유"), entity.ingredients)
        assertEquals(listOf(2, 3), entity.ingredientTypes)
        assertEquals("2025-01-02", entity.createdAt)
    }

    @Test
    fun `UserEntity를 Domain User로 변환해야 한다`() {
        // Given
        val entity = UserEntity(
            id = "user1",
            accessToken = "access123",
            refreshToken = "refresh456",
            username = "테스터",
            accountType = "KAKAO",
            defaultFridgeId = "fridge1"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("user1", domain.id)
        assertEquals("access123", domain.accessToken)
        assertEquals("refresh456", domain.refreshToken)
        assertEquals("테스터", domain.username)
        assertEquals("KAKAO", domain.accountType)
        assertEquals("fridge1", domain.defaultFridgeId)
    }

    @Test
    fun `UserSettingEntity를 Domain UserSetting으로 변환해야 한다`() {
        // Given
        val entity = UserSettingEntity(
            receiveNotification = true,
            autoDeleteExpiredFoods = false,
            useAllIngredients = true
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(true, domain.receiveNotification)
        assertEquals(false, domain.autoDeleteExpiredFoods)
        assertEquals(true, domain.useAllIngredients)
    }

    @Test
    fun `Domain Food를 Presentation FoodModel로 변환해야 한다`() {
        // Given
        val domain = Food(
            id = "food1",
            fridgeId = "fridge1",
            itemName = "사과",
            expiryDate = "2025-12-31",
            categoryId = 1,
            count = 5,
            createdAt = "2025-01-01"
        )

        // When
        val presentation = domain.toPresentation()

        // Then
        assertEquals("food1", presentation.id)
        assertEquals("fridge1", presentation.fridgeId)
        assertEquals("사과", presentation.itemName)
        assertEquals("2025-12-31", presentation.expiryDate)
        assertEquals(1, presentation.categoryId)
        assertEquals(5, presentation.count)
        assertEquals("2025-01-01", presentation.createdAt)
    }

    @Test
    fun `Domain Recipe를 Presentation RecipeModel로 변환해야 한다`() {
        // Given
        val domain = Recipe(
            id = "recipe1",
            recipeImageUrl = "https://example.com/recipe.jpg",
            recipeName = "맛있는 레시피",
            recipeContent = "레시피 내용",
            ingredients = listOf("재료1", "재료2"),
            ingredientTypes = listOf(1, 2),
            createdAt = "2025-01-01"
        )

        // When
        val presentation = domain.toPresentation()

        // Then
        assertEquals("recipe1", presentation.id)
        assertEquals("https://example.com/recipe.jpg", presentation.recipeImageUrl)
        assertEquals("맛있는 레시피", presentation.recipeName)
        assertEquals("레시피 내용", presentation.recipeContent)
        assertEquals(listOf("재료1", "재료2"), presentation.ingredients)
        assertEquals(listOf(1, 2), presentation.ingredientTypes)
        assertEquals("2025-01-01", presentation.createdAt)
    }

    @Test
    fun `빈 문자열 매핑이 올바르게 작동해야 한다`() {
        // Given
        val domain = User(
            id = "",
            accessToken = "",
            refreshToken = "",
            username = "",
            accountType = "",
            defaultFridgeId = ""
        )

        // When
        val entity = domain.toEntity()
        val converted = entity.toDomain()

        // Then
        assertEquals(domain, converted)
    }

    @Test
    fun `null 가능한 필드가 있는 매핑이 올바르게 작동해야 한다`() {
        // Given
        val domain = Food(
            id = "",
            fridgeId = "",
            itemName = "",
            expiryDate = "",
            categoryId = 0,
            count = 0,
            createdAt = ""
        )

        // When
        val entity = domain.toEntity()
        val converted = entity.toDomain()

        // Then
        assertEquals(domain, converted)
    }
}
