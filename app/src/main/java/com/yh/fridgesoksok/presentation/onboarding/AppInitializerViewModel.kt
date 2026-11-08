package com.yh.fridgesoksok.presentation.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.usecase.DeleteLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.GetCountLocalFoodsUseCase
import com.yh.fridgesoksok.domain.usecase.InitializeLocalFoodsFromCsvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

private const val TAG = "AppInitializer"
private const val INITIAL_CSV_FILE = "initial.csv"
private const val MAX_DATA_DIFFERENCE_THRESHOLD = 30

// 프로세스 전체에서 공유하는 초기화 락
private val initializationMutex = Mutex()

/**
 * 앱 초기화를 담당하는 ViewModel
 * 앱 최초 실행 시 검색어 추천을 위한 로컬 음식 데이터를 초기화
 */
@HiltViewModel
class AppInitializerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCountLocalFoodsUseCase: GetCountLocalFoodsUseCase,
    private val initializeLocalFoodsFromCsvUseCase: InitializeLocalFoodsFromCsvUseCase,
    private val deleteLocalFoodsUseCase: DeleteLocalFoodsUseCase,
) : ViewModel() {

    private val _isInitializationCompleted = MutableStateFlow(false)
    val isInitializationCompleted = _isInitializationCompleted.asStateFlow()

    private val _initializationProgress = MutableStateFlow(0)
    val initializationProgress = _initializationProgress.asStateFlow()

    private val _processingStatus = MutableStateFlow("")
    val processingStatus = _processingStatus.asStateFlow()

    private var started = false

    /**
     * 로컬 데이터 초기화 시작 (화면당 한 번만 실행)
     */
    fun start() {
        if (started) return
        started = true
        Logger.i(TAG, "로컬 데이터 초기화 시작")
        _initializationProgress.value = 0
        checkAndInitialize()
    }

    /**
     * 로컬 DB의 데이터 개수를 확인하여 초기화 필요 여부 판단
     */
    private fun checkAndInitialize() {
        viewModelScope.launch {
            // Activity 가 겹쳐 생성되어도 개수 확인~삽입이 동시에 실행되지 않도록 프로세스 단위로 직렬화
            initializationMutex.withLock {
                checkAndInitializeLocked()
            }
        }
    }

    private suspend fun checkAndInitializeLocked() {
        try {
            _initializationProgress.value = 10

            // DB 개수 확인
            var dbCount = 0
            getCountLocalFoodsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        dbCount = result.data ?: 0
                    }
                    is Resource.Error -> {
                        Logger.e(TAG, "로컬 데이터 개수 조회 실패: ${result.message}")
                    }
                    is Resource.Loading -> Unit
                }
            }

            _initializationProgress.value = 30

            // CSV 개수 확인
            val expectedCount = withContext(Dispatchers.IO) {
                getLineCountFromAssets(INITIAL_CSV_FILE)
            }

            _initializationProgress.value = 40

            val difference = abs(dbCount - expectedCount)
            Logger.d(TAG, "DB 데이터 수: $dbCount, CSV 데이터 수: $expectedCount, 차이: $difference")

            // 데이터 개수 차이가 임계값을 초과하면 재초기화
            if (difference > MAX_DATA_DIFFERENCE_THRESHOLD) {
                Logger.i(TAG, "데이터 차이가 크므로 재초기화 수행")
                initializeFoodsFromCsv()
            } else {
                Logger.d(TAG, "로컬 데이터 초기화 스킵")
                _initializationProgress.value = 100
                completeInitialization()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, "초기화 확인 중 예외 발생: ${e.message}", e)
            _initializationProgress.value = 100
            completeInitialization()
        }
    }

    /**
     * CSV 파일에서 음식 데이터를 읽어 DB에 초기화
     */
    private suspend fun initializeFoodsFromCsv() {
        try {
            val parsedFoods = withContext(Dispatchers.IO) {
                parseCsvFromAssets()
            }
            _initializationProgress.value = 50

            if (parsedFoods.isEmpty()) {
                Logger.e(TAG, "CSV 파싱 결과가 비어있음")
                _initializationProgress.value = 100
                completeInitialization()
                return
            }

            Logger.d(TAG, "CSV에서 ${parsedFoods.size}개 항목 파싱 완료")

            // 기존 데이터를 지우고 다시 삽입 (중단된 초기화로 남은 데이터에 중복 추가되는 것을 방지)
            deleteLocalFoodsUseCase().first { it !is Resource.Loading }

            // 배치 단위로 삽입하면서 진행률 업데이트
            val batchSize = 100
            val totalCount = parsedFoods.size
            var processedCount = 0

            parsedFoods.chunked(batchSize).forEach { batch ->
                initializeLocalFoodsFromCsvUseCase(batch).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            processedCount += batch.size
                            // 50%부터 100%까지 매핑 (파싱이 50%까지 차지)
                            val progress = 50 + (processedCount * 50 / totalCount)
                            _initializationProgress.value = progress
                            _processingStatus.value = "$processedCount / $totalCount"
                        }
                        is Resource.Error -> {
                            Logger.e(TAG, "배치 삽입 실패: ${result.message}")
                        }
                        is Resource.Loading -> Unit
                    }
                }
            }

            _processingStatus.value = ""

            Logger.i(TAG, "로컬 데이터 초기화 완료")
            _initializationProgress.value = 100
            completeInitialization()

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(TAG, "CSV 파싱 중 예외 발생: ${e.message}", e)
            _initializationProgress.value = 100
            completeInitialization()
        }
    }

    /**
     * CSV 파일 파싱
     * 형식: 음식명,유통기한(시간),카테고리ID
     */
    private fun parseCsvFromAssets(): List<LocalFood> {
        return try {
            context.assets.open(INITIAL_CSV_FILE).use { inputStream ->
                inputStream.bufferedReader().readLines().mapNotNull { line ->
                    parseCsvLine(line)
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "CSV 파일 읽기 실패: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * CSV 한 줄을 파싱하여 LocalFood 객체로 변환
     */
    private fun parseCsvLine(line: String): LocalFood? {
        val tokens = line.split(",")
        if (tokens.size != 3) {
            Logger.d(TAG, "잘못된 CSV 형식: $line")
            return null
        }

        val itemName = tokens[0].trim()
        val hours = tokens[1].trim().toIntOrNull()
        val categoryId = tokens[2].trim().toIntOrNull()

        if (hours == null || categoryId == null) {
            Logger.d(TAG, "숫자 변환 실패: $line")
            return null
        }

        return LocalFood(itemName, categoryId, hours)
    }

    /**
     * Assets 파일의 라인 수 카운트
     */
    private fun getLineCountFromAssets(fileName: String): Int {
        return try {
            context.assets.open(fileName).use { inputStream ->
                inputStream.bufferedReader().useLines { lines ->
                    lines.count()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "파일 라인 수 카운트 실패: ${e.message}", e)
            0
        }
    }

    /**
     * 초기화 완료 처리
     */
    private fun completeInitialization() {
        _isInitializationCompleted.value = true
    }
}