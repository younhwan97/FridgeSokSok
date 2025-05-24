package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Logger
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
import kotlinx.coroutines.launch
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
        Logger.i("Login", "로그인 시도 $channel")

        createUserOnChannelUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data == null) {
                        fail("채널 응답 데이터 없음")
                        return@onEach
                    }

                    Logger.d("Login", "채널 로그인 성공 $data")
                    val user = UserModel.fromLocal(data)

                    if (user.accountType == "temp") {
                        getUserDefaultFridge(user)
                    } else {
                        createUserOnServer(user)
                    }
                }

                is Resource.Error -> fail("채널 로그인 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // (채널에서 생성한 토큰을 활용해 서버에서) 유저 생성
    private fun createUserOnServer(user: UserModel) {
        createUserOnServerUseCase(user = user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data == null) {
                        fail("서버 응답 데이터 없음")
                        return@onEach
                    }

                    Logger.d("Login", "서버 유저 생성 성공 $data")
                    val updatedUser = user.withUpdatedUserInfo(
                        newAccess = data.accessToken.orEmpty(),
                        newRefresh = data.refreshToken.orEmpty(),
                        newName = data.username.orEmpty(),
                        newAccountType = data.accountType.orEmpty()
                    )
                    getUserDefaultFridge(updatedUser)
                }

                is Resource.Error -> fail("서버 유저 생성 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data == null) {
                        fail("기본 냉장고ID 없음 (data=null)")
                        return@onEach
                    }

                    val updatedUser = user.withDefaultFridgeId(data.id)
                    saveUserUseCase(updatedUser.toDomain())
                    _state.value = LoginState.Success

                    Logger.d("Login", "유저정보 셋팅 성공 $updatedUser")
                    Logger.i("Login", "로그인 성공: id=${updatedUser.id}, name=${updatedUser.username}")
                }

                is Resource.Error -> fail("기본 냉장고ID 조회 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun fail(reason: String) {
        Logger.e("Login", "$reason → 로그인 실패")
        _state.value = LoginState.Error

        viewModelScope.launch {
            _snackBarMessages.emit("로그인 실패")
        }
    }
}