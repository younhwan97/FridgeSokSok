package com.yh.fridgesoksok.data.impl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.local.LocalFoodDataSource
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.local.impl.LocalFoodDataSourceImpl
import com.yh.fridgesoksok.local.room.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodRepositoryIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var localDataSource: LocalFoodDataSource
    private lateinit var repository: FoodRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        localDataSource = LocalFoodDataSourceImpl(database.foodDao())

        // Remote DataSource는 테스트에서 제외하고 Local만 테스트
        repository = FoodRepositoryImpl(
            remoteFoodDataSource = FakeRemoteFoodDataSource(),
            localFoodDataSource = localDataSource
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun initializeLocalFoods_성공적으로_저장되어야_한다() = runBlocking {
        // Given
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("바나나", 2, 12),
            LocalFood("오렌지", 3, 36)
        )

        // When
        val result = repository.initializeLocalFoods(foods).first()

        // Then
        assertTrue(result is Resource.Success)
        assertTrue((result as Resource.Success).data == true)
    }

    @Test
    fun getCountLocalFoods_저장된_개수를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("바나나", 2, 12),
            LocalFood("오렌지", 3, 36)
        )
        repository.initializeLocalFoods(foods).first()

        // When
        val result = repository.getCountLocalFoods().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(3, (result as Resource.Success).data)
    }

    @Test
    fun searchLocalFoods_키워드로_검색해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("사과주스", 1, 48),
            LocalFood("바나나", 2, 12),
            LocalFood("오렌지", 3, 36)
        )
        repository.initializeLocalFoods(foods).first()

        // When
        val result = repository.searchLocalFoods("사과").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size)
        assertTrue(result.data?.all { it.itemName.contains("사과") } == true)
    }

    @Test
    fun deleteLocalFoods_모든_데이터를_삭제해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("바나나", 2, 12),
            LocalFood("오렌지", 3, 36)
        )
        repository.initializeLocalFoods(foods).first()

        // When
        val deleteResult = repository.deleteLocalFoods().first()
        val countResult = repository.getCountLocalFoods().first()

        // Then
        assertTrue(deleteResult is Resource.Success)
        assertEquals(3, (deleteResult as Resource.Success).data)
        assertTrue(countResult is Resource.Success)
        assertEquals(0, (countResult as Resource.Success).data)
    }

    @Test
    fun searchLocalFoods_빈_검색어는_모든_결과를_반환해야_한다() = runBlocking {
        // Given
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("바나나", 2, 12),
            LocalFood("오렌지", 3, 36)
        )
        repository.initializeLocalFoods(foods).first()

        // When
        val result = repository.searchLocalFoods("").first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(3, (result as Resource.Success).data?.size)
    }

    @Test
    fun initializeLocalFoods_빈_리스트도_처리해야_한다() = runBlocking {
        // Given
        val foods = emptyList<LocalFood>()

        // When
        val result = repository.initializeLocalFoods(foods).first()
        val countResult = repository.getCountLocalFoods().first()

        // Then
        assertTrue(result is Resource.Success)
        assertTrue(countResult is Resource.Success)
        assertEquals(0, (countResult as Resource.Success).data)
    }

    @Test
    fun searchLocalFoods_대량_데이터에서_검색해야_한다() = runBlocking {
        // Given
        val foods = (1..500).map {
            LocalFood("식품$it", it % 10, it % 100)
        }
        repository.initializeLocalFoods(foods).first()

        // When
        val result = repository.searchLocalFoods("식품1").first()

        // Then
        assertTrue(result is Resource.Success)
        // "식품1", "식품10", "식품11", ... "식품19", "식품100", ... 등이 매칭됨
        assertTrue((result as Resource.Success).data?.size!! > 0)
    }

    @Test
    fun getCountLocalFoods_빈_데이터베이스는_0을_반환해야_한다() = runBlocking {
        // When
        val result = repository.getCountLocalFoods().first()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(0, (result as Resource.Success).data)
    }

    // Fake Remote DataSource for testing
    private class FakeRemoteFoodDataSource : com.yh.fridgesoksok.data.remote.RemoteFoodDataSource {
        override suspend fun addFoods(fridgeId: String, foods: List<com.yh.fridgesoksok.data.model.FoodEntity>) = emptyList<com.yh.fridgesoksok.data.model.FoodEntity>()
        override suspend fun getFoods(fridgeId: String) = emptyList<com.yh.fridgesoksok.data.model.FoodEntity>()
        override suspend fun updateFood(food: com.yh.fridgesoksok.data.model.FoodEntity) = food
        override suspend fun deleteFood(foodId: String) = true
        override suspend fun uploadReceiptImage(image: ByteArray) = emptyList<com.yh.fridgesoksok.data.model.ReceiptEntity>()
    }
}
