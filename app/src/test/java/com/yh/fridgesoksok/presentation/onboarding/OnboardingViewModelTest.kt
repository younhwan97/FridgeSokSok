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
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import org.junit.Assert.assertNotNull
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
            validateUserTokenUseCase = ValidateUserTokenUseCase(fakeUserRepository),
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
}
