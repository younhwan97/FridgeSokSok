package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FoodModelTest {

    private fun dDayAfter(days: Long): Long {
        val expiry = LocalDate.now().plusDays(days).format(DateFormatter.yyyyMMdd)
        return Food("id", "fridge", "우유", expiry, 6, 1, expiry).toPresentation().dDay
    }

    @Test
    fun `D-day는 실제 날짜 차이여야 한다`() {
        // 개월×30 방식이면 31일 달을 지날 때 하루씩 틀림
        listOf(0L, 1L, 29L, 30L, 31L, 61L, 90L, 365L).forEach { days ->
            assertEquals("+$days 일", days, dDayAfter(days))
        }
    }

    @Test
    fun `지난 소비기한은 음수여야 한다`() {
        assertEquals(-3L, dDayAfter(-3))
        assertEquals(-45L, dDayAfter(-45))
    }
}
