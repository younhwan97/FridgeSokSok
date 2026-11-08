package com.yh.fridgesoksok.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yh.fridgesoksok.local.model.FoodLocal
import com.yh.fridgesoksok.local.room.dao.FoodDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var foodDao: FoodDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        foodDao = database.foodDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFoods_성공적으로_저장되어야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "사과", 1, 24),
            FoodLocal(0, "바나나", 2, 12),
            FoodLocal(0, "오렌지", 3, 36)
        )

        // When
        foodDao.insertAll(foods)
        val count = foodDao.getCount()

        // Then
        assertEquals(3, count)
    }

    @Test
    fun getCount_빈_데이터베이스는_0을_반환해야_한다() = runBlocking {
        // When
        val count = foodDao.getCount()

        // Then
        assertEquals(0, count)
    }

    @Test
    fun searchFoods_키워드로_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "사과", 1, 24),
            FoodLocal(0, "사과주스", 1, 48),
            FoodLocal(0, "바나나", 2, 12),
            FoodLocal(0, "오렌지", 3, 36)
        )
        foodDao.insertAll(foods)

        // When
        val result = foodDao.searchFoodByKeyword("사과")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.itemName.contains("사과") })
    }

    @Test
    fun searchFoods_대소문자_구분없이_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "Apple", 1, 24),
            FoodLocal(0, "APPLE", 1, 24),
            FoodLocal(0, "apple", 1, 24),
            FoodLocal(0, "Banana", 2, 12)
        )
        foodDao.insertAll(foods)

        // When
        val result = foodDao.searchFoodByKeyword("apple")

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun deleteFoods_모든_데이터를_삭제해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "사과", 1, 24),
            FoodLocal(0, "바나나", 2, 12),
            FoodLocal(0, "오렌지", 3, 36)
        )
        foodDao.insertAll(foods)

        // When
        val deletedCount = foodDao.deleteFoods()
        val count = foodDao.getCount()

        // Then
        assertEquals(3, deletedCount)
        assertEquals(0, count)
    }

    @Test
    fun insertFoods_같은_이름도_별도_행으로_저장되어야_한다() = runBlocking {
        // Given
        val food1 = FoodLocal(0, "사과", 1, 24)
        val food2 = FoodLocal(0, "사과", 2, 36) // 같은 이름, 다른 카테고리

        // When
        foodDao.insertAll(listOf(food1))
        foodDao.insertAll(listOf(food2))
        val count = foodDao.getCount()

        // Then
        // id 가 자동 생성이라 이름이 같아도 충돌하지 않음 (CSV 초기화는 전체 삭제 후 삽입)
        assertEquals(2, count)
    }

    @Test
    fun searchFoods_빈_검색어는_모든_결과를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "사과", 1, 24),
            FoodLocal(0, "바나나", 2, 12),
            FoodLocal(0, "오렌지", 3, 36)
        )
        foodDao.insertAll(foods)

        // When
        val result = foodDao.searchFoodByKeyword("")

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun searchFoods_일치하는_결과가_없으면_빈_리스트를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "사과", 1, 24),
            FoodLocal(0, "바나나", 2, 12)
        )
        foodDao.insertAll(foods)

        // When
        val result = foodDao.searchFoodByKeyword("포도")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertFoods_대량_데이터를_처리해야_한다() = runBlocking {
        // Given
        val foods = (1..1000).map {
            FoodLocal(0, "식품$it", it % 10, it % 100)
        }

        // When
        foodDao.insertAll(foods)
        val count = foodDao.getCount()

        // Then
        assertEquals(1000, count)
    }

    @Test
    fun searchFoods_앞부분_일치로_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal(0, "딸기우유", 1, 48),
            FoodLocal(0, "바나나우유", 1, 48),
            FoodLocal(0, "초코우유", 1, 48),
            FoodLocal(0, "우유", 1, 72),
            FoodLocal(0, "딸기", 2, 24)
        )
        foodDao.insertAll(foods)

        // When
        val result = foodDao.searchFoodByKeyword("우유")

        // Then
        // 검색어 추천은 입력값으로 시작하는 항목만 반환 (LIKE 'query%')
        assertEquals(listOf("우유"), result.map { it.itemName })
    }
}
