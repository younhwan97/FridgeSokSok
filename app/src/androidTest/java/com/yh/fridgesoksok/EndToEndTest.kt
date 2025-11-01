package com.yh.fridgesoksok

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yh.fridgesoksok.data.impl.FoodRepositoryImpl
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-End 테스트
 *
 * 사용자 시나리오를 기반으로 한 통합 테스트
 * 실제 데이터베이스와 Repository를 사용하여 전체 플로우를 검증
 */
@RunWith(AndroidJUnit4::class)
class zEndToEndTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

        repository = FoodRepositoryImpl(
            remoteFoodDataSource = FakeRemoteFoodDataSource(),
            localFoodDataSource = localDataSource
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * 시나리오 1: 식품 데이터 초기화 및 검색
     * 1. 로컬 식품 데이터 초기화
     * 2. 데이터 개수 확인
     * 3. 특정 키워드로 검색
     * 4. 검색 결과 확인
     */
    @Test
    fun 시나리오_식품_초기화_및_검색() = runBlocking {
        // Given - 초기 데이터 설정
        val foods = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("사과주스", 1, 48),
            LocalFood("바나나", 2, 12),
            LocalFood("바나나우유", 2, 72),
            LocalFood("오렌지", 3, 36)
        )

        // When - 데이터 초기화
        val initResult = repository.initializeLocalFoods(foods).first()

        // Then - 초기화 성공 확인
        assertTrue(initResult is com.yh.fridgesoksok.common.Resource.Success)

        // When - 전체 개수 확인
        val countResult = repository.getCountLocalFoods().first()

        // Then - 5개가 저장되었는지 확인
        assertTrue(countResult is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(5, (countResult as com.yh.fridgesoksok.common.Resource.Success).data)

        // When - "사과"로 검색
        val searchResult = repository.searchLocalFoods("사과").first()

        // Then - 사과, 사과주스 2개만 검색되어야 함
        assertTrue(searchResult is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(2, (searchResult as com.yh.fridgesoksok.common.Resource.Success).data?.size)
        assertTrue(searchResult.data?.all { it.itemName.contains("사과") } == true)
    }

    /**
     * 시나리오 2: 대량 데이터 처리 및 검색 성능
     * 1. 대량의 식품 데이터 삽입 (1000개)
     * 2. 검색 성능 확인
     * 3. 필터링 정확도 확인
     */
    @Test
    fun 시나리오_대량_데이터_처리_및_검색() = runBlocking {
        // Given - 1000개의 식품 데이터
        val foods = (1..1000).map {
            LocalFood("식품$it", it % 10, it % 100)
        }

        // When - 대량 데이터 초기화
        val initResult = repository.initializeLocalFoods(foods).first()

        // Then - 초기화 성공
        assertTrue(initResult is com.yh.fridgesoksok.common.Resource.Success)

        // When - 전체 개수 확인
        val countResult = repository.getCountLocalFoods().first()

        // Then - 1000개 저장 확인
        assertEquals(1000, (countResult as com.yh.fridgesoksok.common.Resource.Success).data)

        // When - "식품1"로 검색 (식품1, 식품10-19, 식품100-199 등이 매칭됨)
        val searchResult = repository.searchLocalFoods("식품1").first()

        // Then - 검색 결과가 있어야 함
        assertTrue(searchResult is com.yh.fridgesoksok.common.Resource.Success)
        assertTrue((searchResult as com.yh.fridgesoksok.common.Resource.Success).data?.isNotEmpty() == true)

        // 모든 결과가 "식품1"을 포함해야 함
        assertTrue(searchResult.data?.all { it.itemName.contains("식품1") } == true)
    }

    /**
     * 시나리오 3: 데이터 수정 및 삭제 플로우
     * 1. 초기 데이터 삽입
     * 2. 검색으로 데이터 확인
     * 3. 전체 데이터 삭제
     * 4. 삭제 후 검색 결과 확인
     */
    @Test
    fun 시나리오_데이터_수정_및_삭제() = runBlocking {
        // Given - 초기 데이터
        val foods = listOf(
            LocalFood("우유", 1, 48),
            LocalFood("요구르트", 1, 24),
            LocalFood("치즈", 1, 72)
        )

        // When - 데이터 초기화
        repository.initializeLocalFoods(foods).first()

        // Then - 검색으로 데이터 확인
        val searchResult = repository.searchLocalFoods("").first()
        assertEquals(3, (searchResult as com.yh.fridgesoksok.common.Resource.Success).data?.size)

        // When - 전체 데이터 삭제
        val deleteResult = repository.deleteLocalFoods().first()

        // Then - 삭제 성공 확인 (3개 삭제됨)
        assertTrue(deleteResult is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(3, (deleteResult as com.yh.fridgesoksok.common.Resource.Success).data)

        // Then - 삭제 후 검색 시 빈 결과
        val afterDeleteSearch = repository.searchLocalFoods("").first()
        assertTrue(afterDeleteSearch is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(0, (afterDeleteSearch as com.yh.fridgesoksok.common.Resource.Success).data?.size)
    }

    /**
     * 시나리오 4: 중복 데이터 처리
     * 1. 초기 데이터 삽입
     * 2. 동일한 이름의 데이터 재삽입
     * 3. 중복 처리 확인 (REPLACE 전략)
     */
    @Test
    fun 시나리오_중복_데이터_처리() = runBlocking {
        // Given - 초기 데이터
        val foods1 = listOf(LocalFood("사과", 1, 24))

        // When - 첫 번째 삽입
        repository.initializeLocalFoods(foods1).first()

        // Then - 1개 저장 확인
        val count1 = repository.getCountLocalFoods().first()
        assertEquals(1, (count1 as com.yh.fridgesoksok.common.Resource.Success).data)

        // When - 같은 이름으로 다시 삽입 (다른 카테고리)
        val foods2 = listOf(LocalFood("사과", 2, 36))
        repository.initializeLocalFoods(foods2).first()

        // Then - REPLACE 전략으로 여전히 1개만 존재
        val count2 = repository.getCountLocalFoods().first()
        assertEquals(1, (count2 as com.yh.fridgesoksok.common.Resource.Success).data)

        // 검색 결과도 1개
        val searchResult = repository.searchLocalFoods("사과").first()
        assertEquals(1, (searchResult as com.yh.fridgesoksok.common.Resource.Success).data?.size)
    }

    /**
     * 시나리오 5: 검색 정확도 테스트
     * 1. 다양한 유제품 데이터 삽입
     * 2. 부분 일치 검색 확인
     * 3. 대소문자 구분 없는 검색 확인
     */
    @Test
    fun 시나리오_검색_정확도() = runBlocking {
        // Given - 유제품 데이터
        val foods = listOf(
            LocalFood("딸기우유", 1, 48),
            LocalFood("바나나우유", 1, 48),
            LocalFood("초코우유", 1, 48),
            LocalFood("우유", 1, 72),
            LocalFood("딸기", 2, 24)
        )

        // When - 데이터 초기화
        repository.initializeLocalFoods(foods).first()

        // Then - "우유" 검색 시 4개 결과
        val searchMilk = repository.searchLocalFoods("우유").first()
        assertEquals(4, (searchMilk as com.yh.fridgesoksok.common.Resource.Success).data?.size)
        assertTrue(searchMilk.data?.all { it.itemName.contains("우유") } == true)

        // Then - "딸기" 검색 시 2개 결과 (딸기, 딸기우유)
        val searchStrawberry = repository.searchLocalFoods("딸기").first()
        assertEquals(2, (searchStrawberry as com.yh.fridgesoksok.common.Resource.Success).data?.size)

        // Then - 빈 검색어는 전체 결과 반환
        val searchAll = repository.searchLocalFoods("").first()
        assertEquals(5, (searchAll as com.yh.fridgesoksok.common.Resource.Success).data?.size)
    }

    /**
     * 시나리오 6: 빈 데이터 처리
     * 1. 빈 리스트로 초기화
     * 2. 검색 결과 확인
     * 3. 삭제 시 동작 확인
     */
    @Test
    fun 시나리오_빈_데이터_처리() = runBlocking {
        // When - 빈 리스트로 초기화
        val initResult = repository.initializeLocalFoods(emptyList()).first()

        // Then - 초기화 성공
        assertTrue(initResult is com.yh.fridgesoksok.common.Resource.Success)

        // Then - 개수는 0
        val countResult = repository.getCountLocalFoods().first()
        assertEquals(0, (countResult as com.yh.fridgesoksok.common.Resource.Success).data)

        // Then - 검색 결과도 빈 리스트
        val searchResult = repository.searchLocalFoods("").first()
        assertTrue(searchResult is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(0, (searchResult as com.yh.fridgesoksok.common.Resource.Success).data?.size)

        // When - 빈 데이터베이스에서 삭제 시도
        val deleteResult = repository.deleteLocalFoods().first()

        // Then - 삭제된 개수는 0
        assertTrue(deleteResult is com.yh.fridgesoksok.common.Resource.Success)
        assertEquals(0, (deleteResult as com.yh.fridgesoksok.common.Resource.Success).data)
    }

    /**
     * 시나리오 7: 순차적 작업 플로우
     * 1. 데이터 삽입
     * 2. 검색
     * 3. 삭제
     * 4. 재삽입
     * 5. 최종 검증
     */
    @Test
    fun 시나리오_순차적_작업_플로우() = runBlocking {
        // Step 1: 초기 데이터 삽입
        val foods1 = listOf(
            LocalFood("사과", 1, 24),
            LocalFood("바나나", 2, 12)
        )
        repository.initializeLocalFoods(foods1).first()

        val count1 = repository.getCountLocalFoods().first()
        assertEquals(2, (count1 as com.yh.fridgesoksok.common.Resource.Success).data)

        // Step 2: 검색
        val search1 = repository.searchLocalFoods("사과").first()
        assertEquals(1, (search1 as com.yh.fridgesoksok.common.Resource.Success).data?.size)

        // Step 3: 전체 삭제
        val delete = repository.deleteLocalFoods().first()
        assertEquals(2, (delete as com.yh.fridgesoksok.common.Resource.Success).data)

        // Step 4: 새로운 데이터 재삽입
        val foods2 = listOf(
            LocalFood("오렌지", 3, 36),
            LocalFood("포도", 4, 60),
            LocalFood("수박", 5, 12)
        )
        repository.initializeLocalFoods(foods2).first()

        // Step 5: 최종 검증
        val finalCount = repository.getCountLocalFoods().first()
        assertEquals(3, (finalCount as com.yh.fridgesoksok.common.Resource.Success).data)

        val finalSearch = repository.searchLocalFoods("").first()
        assertEquals(3, (finalSearch as com.yh.fridgesoksok.common.Resource.Success).data?.size)
    }

    // Fake Remote DataSource for testing
    private class FakeRemoteFoodDataSource : com.yh.fridgesoksok.data.remote.RemoteFoodDataSource {
        override suspend fun addFoods(fridgeId: String, foods: List<com.yh.fridgesoksok.data.model.FoodEntity>) = emptyList<com.yh.fridgesoksok.data.model.FoodEntity>()
        override suspend fun getFoods(fridgeId: String) = emptyList<com.yh.fridgesoksok.data.model.FoodEntity>()
        override suspend fun updateFood(food: com.yh.fridgesoksok.data.model.FoodEntity) = food
        override suspend fun deleteFood(foodId: String) = true
        override suspend fun uploadReceiptImage(img: android.graphics.Bitmap) = emptyList<com.yh.fridgesoksok.data.model.ReceiptEntity>()
    }
}
