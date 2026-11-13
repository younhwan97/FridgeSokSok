package com.yh.fridgesoksok.remote.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 실서버(soksok.io) 응답 예시 기준 파싱 검증
 */
class FoodFridgeResponseTest {

    private val gson = Gson()

    @Test
    fun `음식 응답의 registerDate를 등록일로 읽어야 한다`() {
        val json = """
            {"message":"Success","data":[{"id":"0eacb2f7-91f0-415c-bf83-19251dfab51e","itemName":"test",
            "registerDate":"2026-11-13","expiryDate":"2026-11-27","count":3,"categoryId":11}],"status":200}
        """.trimIndent()
        val type = object : TypeToken<CommonResponse<List<FoodResponse>>>() {}.type
        val food = gson.fromJson<CommonResponse<List<FoodResponse>>>(json, type).data!!.single().toEntity()

        assertEquals("test", food.itemName)
        assertEquals("20261113", food.createdAt)
        assertEquals("20261127", food.expiryDate)
        assertEquals(3, food.count)
        assertEquals(11, food.categoryId)
    }

    @Test
    fun `냉장고 응답의 name과 ISO 일시 createdAt을 읽어야 한다`() {
        val json = """
            {"message":"Success","data":{"id":"7fdd9c51-f87c-4584-8611-9be493794e3e","name":"Default Fridge",
            "userId":"5b580e4c-1880-45dc-9fca-2ed1f3d576a7","createdAt":"2026-11-13T19:39:22.948296Z"},"status":200}
        """.trimIndent()
        val type = object : TypeToken<CommonResponse<FridgeResponse>>() {}.type
        val fridge = gson.fromJson<CommonResponse<FridgeResponse>>(json, type).data!!.toEntity()

        assertEquals("7fdd9c51-f87c-4584-8611-9be493794e3e", fridge.id)
        assertEquals("Default Fridge", fridge.fridgeName)
        assertEquals("20261113", fridge.createdAt)
    }
}
