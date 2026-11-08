package com.yh.fridgesoksok.remote.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Test

/**
 * 서버 변경 문서(receipts/upload 응답 예시) 기준 파싱 검증
 */
class ReceiptResponseTest {

    private val gson = Gson()
    private val type = object : TypeToken<CommonResponse<List<ReceiptResponse>>>() {}.type

    private val body = """
        {
          "message": "Success",
          "status": 200,
          "data": [
            { "id": -168, "itemName": "삼겹살", "expirationDescription": "냉장 3~5일", "foodType": "육류",
              "shelfLifeHours": 72, "categoryId": 11, "count": 1, "storage": "냉장", "dateBasis": "SAFETY",
              "frozenShelfLifeHours": 2880, "frozenDescription": "냉동 4~12개월" },
            { "id": 983327, "itemName": "서울우유", "expirationDescription": "제조일로부터 140일", "foodType": "우유",
              "shelfLifeHours": 3360, "categoryId": 6, "count": 2, "storage": null, "dateBasis": "LABEL",
              "frozenShelfLifeHours": null, "frozenDescription": null },
            { "name": "삼다수", "count": 2 }
          ]
        }
    """.trimIndent()

    private fun parse(json: String = body): List<ReceiptResponse> =
        gson.fromJson<CommonResponse<List<ReceiptResponse>>>(json, type).data.orEmpty()

    @Test
    fun `신선식품의 음수 id를 그대로 읽어야 한다`() {
        val items = parse()

        assertEquals(-168L, items[0].id)
        assertEquals(-168L, items[0].toEntity().id)
    }

    @Test
    fun `새로 추가된 필드가 있어도 기존 필드를 정상적으로 읽어야 한다`() {
        val milk = parse()[1].toEntity()

        assertEquals("서울우유", milk.itemName)
        assertEquals(3360, milk.shelfLifeHours)
        assertEquals(6, milk.categoryId)
        assertEquals(2, milk.count)
    }

    @Test
    fun `미매칭 항목은 name을 이름으로 읽고 유통기한은 null이어야 한다`() {
        val water = parse()[2].toEntity()

        assertEquals("삼다수", water.itemName)
        assertEquals(2, water.count)
        assertNull(water.id)
        assertNull(water.shelfLifeHours)
    }

    @Test
    fun `Int 범위를 넘는 id도 파싱에 실패하지 않아야 한다`() {
        val json = """{"message":"Success","status":200,"data":[{"id":19790421001234,"itemName":"테스트","shelfLifeHours":24,"categoryId":1,"count":1}]}"""

        val item = parse(json).single()

        assertEquals(19790421001234L, item.id)
    }
}
