package com.yh.fridgesoksok.presentation.login

import com.yh.fridgesoksok.MainDispatcherRule
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.usecase.CreateUserOnChannelUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserOnServerUseCase
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.LOGIN_FAILED
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.LOGIN_TOO_MANY_REQUESTS
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.NETWORK_ERROR
import com.yh.fridgesoksok.presentation.common.constants.StringResources.ErrorMessages.SERVER_ERROR
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginErrorHandlingTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var viewModel: LoginViewModel

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

    private fun TestScope.collectMessages(): List<String> {
        val messages = mutableListOf<String>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.errorMessage.collect { messages += it }
        }
        return messages
    }

    @Test
    fun `상태 코드별로 사용자 안내 문구를 구분해야 한다`() {
        assertEquals(LOGIN_TOO_MANY_REQUESTS, loginErrorMessage(429))
        assertEquals(NETWORK_ERROR, loginErrorMessage(Resource.STATUS_NETWORK_ERROR))
        assertEquals(SERVER_ERROR, loginErrorMessage(503))
        assertEquals(LOGIN_FAILED, loginErrorMessage(400))
        assertEquals(LOGIN_FAILED, loginErrorMessage(null))
        assertNull(loginErrorMessage(Resource.STATUS_CANCELLED))
    }

    @Test
    fun `FCM 토큰이 없어도 로그인은 성공해야 한다`() = runTest {
        viewModel.createUserOnChannel(Channel.GUEST)
        mainDispatcherRule.advance()

        assertEquals(LoginState.Success, viewModel.state.value)
    }

    @Test
    fun `요청 과다(429)로 실패하면 개발용 문구가 아닌 안내 문구를 보여줘야 한다`() = runTest {
        val messages = collectMessages()
        fakeUserRepository.channelLoginError = Resource.Error("HTTP 429", status = 429)

        viewModel.createUserOnChannel(Channel.GUEST)
        mainDispatcherRule.advance()

        assertEquals(LoginState.Error, viewModel.state.value)
        assertEquals(listOf(LOGIN_TOO_MANY_REQUESTS), messages)
    }

    @Test
    fun `사용자가 로그인을 취소하면 안내 문구를 보여주지 않아야 한다`() = runTest {
        val messages = collectMessages()
        fakeUserRepository.channelLoginError = Resource.Error("cancelled", status = Resource.STATUS_CANCELLED)

        viewModel.createUserOnChannel(Channel.KAKAO)
        mainDispatcherRule.advance()

        assertEquals(LoginState.Error, viewModel.state.value)
        assertTrue(messages.isEmpty())
    }
}
