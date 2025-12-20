package com.yh.fridgesoksok.presentation.account

import com.yh.fridgesoksok.MainDispatcherRule
import com.yh.fridgesoksok.data.repository.FakeUserRepository
import com.yh.fridgesoksok.domain.usecase.ClearUserUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteUserUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserSettingUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateAutoDeleteExpiredUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateReceiveNotificationUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUseAllIngredientsUseCase
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AccountViewModel
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        viewModel = AccountViewModel(
            clearUserUseCase = ClearUserUseCase(fakeUserRepository),
            deleteUserUseCase = DeleteUserUseCase(fakeUserRepository),
            getUserSettingUseCase = GetUserSettingUseCase(fakeUserRepository),
            updateReceiveNotificationUseCase = UpdateReceiveNotificationUseCase(fakeUserRepository),
            updateAutoDeleteExpiredUseCase = UpdateAutoDeleteExpiredUseCase(fakeUserRepository),
            updateUseAllIngredientsUseCase = UpdateUseAllIngredientsUseCase(fakeUserRepository)
        )
    }

    @Test
    fun `ViewModel이 정상적으로 생성되어야 한다`() {
        // ViewModel 생성 확인
        assertNotNull(viewModel)
    }
}
