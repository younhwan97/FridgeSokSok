package com.yh.fridgesoksok.presentation.login

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.usecase.CreateUserOnChannelUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserOnServerUseCase
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        viewModel = LoginViewModel(
            createUserOnChannelUseCase = CreateUserOnChannelUseCase(fakeUserRepository),
            saveUserUseCase = SaveUserUseCase(fakeUserRepository),
            createUserOnServerUseCase = CreateUserOnServerUseCase(fakeUserRepository),
            getUserDefaultFridgeUseCase = GetUserDefaultFridgeUseCase(fakeUserRepository),
            getLocalUserFcmTokenUseCase = GetLocalUserFcmTokenUseCase(fakeUserRepository),
            updateUserFcmTokenUseCase = UpdateUserFcmTokenUseCase(fakeUserRepository),
            getApiUsageUseCase = GetApiUsageUseCase(fakeUserRepository),
            updateApiUsageUseCase = UpdateApiUsageUseCase(fakeUserRepository)
        )
    }

    @Test
    fun `초기 상태는 Loading이어야 한다`() {
        assertEquals(LoginState.Loading, viewModel.state.value)
    }

    @Test
    fun `ViewModel이 정상적으로 생성되어야 한다`() {
        // ViewModel 생성 확인
        assertEquals(LoginState.Loading, viewModel.state.value)
    }
}
