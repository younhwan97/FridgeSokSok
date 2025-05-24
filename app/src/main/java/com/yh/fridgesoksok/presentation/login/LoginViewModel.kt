package com.yh.fridgesoksok.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.CreateUserOnChannelUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserOnServerUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserOnChannelUseCase: CreateUserOnChannelUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserOnServerUseCase: CreateUserOnServerUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Loading)
    val state = _state.asStateFlow()

    private val _snackBarMessages = MutableSharedFlow<String>()
    val snackBarMessages = _snackBarMessages.asSharedFlow()

    // (네이버, 카카오 등의) 채널로 부터 유저 생성
    fun createUserOnChannel(channel: Channel) {
        createUserOnChannelUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail("채널 로그인 실패")
                is Resource.Success -> result.data.orFail("유효하지 않은 유저 데이터") {
                    val user = UserModel.fromLocal(it)

                    if (user.accountType == "temp") {
                        saveUserUseCase(user.toDomain())
                        getUserDefaultFridge(user)
                    } else {
                        createUserOnServer(user)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // (채널에서 생성한 토큰을 활용해 서버에서) 유저 생성
    private fun createUserOnServer(user: UserModel) {
        createUserOnServerUseCase(user = user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail("서버 유저 생성 실패")
                is Resource.Success -> result.data.orFail("서버 응답 데이터 없음 또는 무효") { serverUser ->
                    val updatedUser = user.withUpdatedUserInfo(
                        newAccess = serverUser.accessToken.orEmpty(),
                        newRefresh = serverUser.refreshToken.orEmpty(),
                        newName = serverUser.username.orEmpty(),
                        newAccountType = serverUser.accountType.orEmpty()
                    )
                    getUserDefaultFridge(updatedUser)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Error -> fail("기본 냉장고 조회 실패")
                is Resource.Success -> result.data.orFail("기본 냉장고 정보가 없음") { fridge ->
                    val updatedUser = user.withDefaultFridgeId(fridge.id)
                    saveUserUseCase(updatedUser.toDomain())
                    _state.value = LoginState.Success
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fail(reason: String) {
        Log.e("Onboarding", "Fail: $reason")
        _state.value = LoginState.Error(reason)
    }

    private inline fun <T> T?.orFail(reason: String, onSuccess: (T) -> Unit) {
        if (this == null) fail(reason) else onSuccess(this)
    }
}