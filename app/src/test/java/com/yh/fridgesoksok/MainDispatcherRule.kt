package com.yh.fridgesoksok

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule that sets the Main dispatcher to a TestDispatcher for unit tests.
 * This allows ViewModels to use viewModelScope which depends on Main dispatcher.
 *
 * Uses StandardTestDispatcher by default to prevent immediate execution of coroutines,
 * which is safer for ViewModels with init blocks that launch coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    // 대기 중인 코루틴을 모두 실행
    fun advance() = testDispatcher.scheduler.advanceUntilIdle()

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
