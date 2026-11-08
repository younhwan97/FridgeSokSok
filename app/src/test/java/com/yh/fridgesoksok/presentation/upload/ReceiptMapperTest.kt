package com.yh.fridgesoksok.presentation.upload

import com.yh.fridgesoksok.domain.model.Receipt
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ReceiptMapperTest {

    private val today = LocalDate.of(2026, 11, 8)

    private fun receipt(name: String, shelfLifeHours: Int?, id: Long? = 1) = Receipt(
        id = id,
        itemName = name,
        expirationDescription = "",
        foodType = "",
        shelfLifeHours = shelfLifeHours,
        categoryId = 1,
        count = 1
    )

    @Test
    fun `유통기한이 있는 항목은 오늘부터 보관 시간만큼 더한 날짜가 되어야 한다`() {
        val food = listOf(receipt("삼겹살", shelfLifeHours = 72, id = -168)).toUploadFoods(today).single()

        assertEquals("20261111", food.expiryDate)
        assertFalse(food.needsExpiryInput)
    }

    @Test
    fun `미매칭 항목은 오늘 날짜로 두고 소비기한 입력 필요로 표시해야 한다`() {
        val food = listOf(receipt("삼다수", shelfLifeHours = null, id = null)).toUploadFoods(today).single()

        assertEquals("삼다수", food.itemName)
        assertEquals("20261108", food.expiryDate)
        assertTrue(food.needsExpiryInput)
    }

    @Test
    fun `이름이 없는 항목은 제외해야 한다`() {
        val foods = listOf(receipt("", shelfLifeHours = null), receipt("우유", shelfLifeHours = 24)).toUploadFoods(today)

        assertEquals(listOf("우유"), foods.map { it.itemName })
    }
}
