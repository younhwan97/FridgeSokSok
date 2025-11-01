package com.yh.fridgesoksok.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yh.fridgesoksok.local.model.FoodLocal
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
            FoodLocal("사과", 1),
            FoodLocal("바나나", 2),
            FoodLocal("오렌지", 3)
        )

        // When
        foodDao.insertFoods(foods)
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
            FoodLocal("사과", 1),
            FoodLocal("사과주스", 1),
            FoodLocal("바나나", 2),
            FoodLocal("오렌지", 3)
        )
        foodDao.insertFoods(foods)

        // When
        val result = foodDao.searchFoods("사과")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.itemName.contains("사과") })
    }

    @Test
    fun searchFoods_대소문자_구분없이_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal("Apple", 1),
            FoodLocal("APPLE", 1),
            FoodLocal("apple", 1),
            FoodLocal("Banana", 2)
        )
        foodDao.insertFoods(foods)

        // When
        val result = foodDao.searchFoods("apple")

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun deleteFoods_모든_데이터를_삭제해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal("사과", 1),
            FoodLocal("바나나", 2),
            FoodLocal("오렌지", 3)
        )
        foodDao.insertFoods(foods)

        // When
        val deletedCount = foodDao.deleteFoods()
        val count = foodDao.getCount()

        // Then
        assertEquals(3, deletedCount)
        assertEquals(0, count)
    }

    @Test
    fun insertFoods_중복된_데이터는_교체되어야_한다() = runBlocking {
        // Given
        val food1 = FoodLocal("사과", 1)
        val food2 = FoodLocal("사과", 2) // 같은 이름, 다른 카테고리

        // When
        foodDao.insertFoods(listOf(food1))
        foodDao.insertFoods(listOf(food2))
        val count = foodDao.getCount()

        // Then
        // REPLACE 전략이므로 1개만 존재해야 함
        assertEquals(1, count)
    }

    @Test
    fun searchFoods_빈_검색어는_모든_결과를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal("사과", 1),
            FoodLocal("바나나", 2),
            FoodLocal("오렌지", 3)
        )
        foodDao.insertFoods(foods)

        // When
        val result = foodDao.searchFoods("")

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun searchFoods_일치하는_결과가_없으면_빈_리스트를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal("사과", 1),
            FoodLocal("바나나", 2)
        )
        foodDao.insertFoods(foods)

        // When
        val result = foodDao.searchFoods("포도")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertFoods_대량_데이터를_처리해야_한다() = runBlocking {
        // Given
        val foods = (1..1000).map {
            FoodLocal("식품$it", it % 10)
        }

        // When
        foodDao.insertFoods(foods)
        val count = foodDao.getCount()

        // Then
        assertEquals(1000, count)
    }

    @Test
    fun searchFoods_부분_일치로_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            FoodLocal("딸기우유", 1),
            FoodLocal("바나나우유", 1),
            FoodLocal("초코우유", 1),
            FoodLocal("우유", 1),
            FoodLocal("딸기", 2)
        )
        foodDao.insertFoods(foods)

        // When
        val result = foodDao.searchFoods("우유")

        // Then
        assertEquals(4, result.size)
        assertTrue(result.all { it.itemName.contains("우유") })
    }
}
