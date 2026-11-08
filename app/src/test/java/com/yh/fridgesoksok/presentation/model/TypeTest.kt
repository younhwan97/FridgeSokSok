package com.yh.fridgesoksok.presentation.model

import org.junit.Assert.*
import org.junit.Test

class TypeTest {

    @Test
    fun `서버에 추가된 카테고리 18~22를 인식해야 한다`() {
        assertEquals(Type.Vegetables, Type.fromId(18))
        assertEquals(Type.Fruits, Type.fromId(19))
        assertEquals(Type.Eggs, Type.fromId(20))
        assertEquals(Type.Alcohol, Type.fromId(21))
        assertEquals(Type.BabyFood, Type.fromId(22))
    }

    @Test
    fun `확장 카테고리가 꺼져 있으면 선택지에서 제외해야 한다`() {
        val selectable = Type.selectable(extendedEnabled = false)

        assertTrue(selectable.none { it.isExtended })
        assertTrue(Type.Meat in selectable)
    }

    @Test
    fun `확장 카테고리가 켜지면 모든 카테고리를 선택지에 포함해야 한다`() {
        assertEquals(Type.entries.toList(), Type.selectable(extendedEnabled = true))
    }

    @Test
    fun `카테고리 id는 중복되지 않아야 한다`() {
        assertEquals(Type.entries.size, Type.entries.map { it.id }.toSet().size)
    }
}
