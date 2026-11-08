package com.yh.fridgesoksok.presentation.onboarding

import com.yh.fridgesoksok.MainDispatcherRule
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.GetServerStatusUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ReissueUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        viewModel = OnboardingViewModel(
            getServerStatusUseCase = GetServerStatusUseCase(fakeUserRepository),
            loadUserUseCase = LoadUserUseCase(fakeUserRepository),
            updateUserUseCase = UpdateUserUseCase(fakeUserRepository),
            reissueUserTokenUseCase = ReissueUserTokenUseCase(fakeUserRepository),
            getUserDefaultFridgeUseCase = GetUserDefaultFridgeUseCase(fakeUserRepository),
            getLocalUserFcmTokenUseCase = GetLocalUserFcmTokenUseCase(fakeUserRepository),
            updateUserFcmTokenUseCase = UpdateUserFcmTokenUseCase(fakeUserRepository),
            getApiUsageUseCase = GetApiUsageUseCase(fakeUserRepository),
            updateApiUsageUseCase = UpdateApiUsageUseCase(fakeUserRepository)
        )
    }

    @Test
    fun `ViewModel이 정상적으로 생성되어야 한다`() {
        // ViewModel 생성 확인
        assertNotNull(viewModel)
    }

    @Test
    fun `start 를 호출하기 전에는 온보딩을 시작하지 않아야 한다`() = runTest {
        mainDispatcherRule.advance()

        assertEquals(0, fakeUserRepository.reissueCallCount)
        assertEquals(OnboardingState.Loading, viewModel.state.value)
    }

    @Test
    fun `start 를 여러 번 호출해도 온보딩은 한 번만 실행되어야 한다`() = runTest {
        viewModel.start()
        viewModel.start()
        mainDispatcherRule.advance()

        assertEquals(1, fakeUserRepository.reissueCallCount)
        assertEquals(OnboardingState.Success, viewModel.state.value)
    }

    @Test
    fun `토큰 검증 호출 없이 재발급만 하고, 저장된 냉장고 id 가 있으면 조회를 생략해야 한다`() = runTest {
        viewModel.start()
        mainDispatcherRule.advance()

        assertEquals(0, fakeUserRepository.validateCallCount)
        assertEquals(1, fakeUserRepository.reissueCallCount)
        assertEquals(0, fakeUserRepository.defaultFridgeCallCount)
        assertEquals(OnboardingState.Success, viewModel.state.value)
    }

    @Test
    fun `저장된 냉장고 id 가 없으면 기본 냉장고를 조회해야 한다`() = runTest {
        runBlocking {
            fakeUserRepository.saveUser(
                User("user-1", "access", "refresh", "tester", "GUEST", defaultFridgeId = "")
            ).first()
        }

        viewModel.start()
        mainDispatcherRule.advance()

        assertEquals(1, fakeUserRepository.defaultFridgeCallCount)
        assertEquals("fridge-1", fakeUserRepository.loadUser().defaultFridgeId)
        assertEquals(OnboardingState.Success, viewModel.state.value)
    }

    @Test
    fun `토큰 재발급이 실패하면 온보딩이 실패해야 한다`() = runTest {
        fakeUserRepository.reissueError = Resource.Error("Invalid or expired refresh token", status = 400)

        viewModel.start()
        mainDispatcherRule.advance()

        assertEquals(OnboardingState.Error, viewModel.state.value)
    }
}
