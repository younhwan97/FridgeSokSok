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
import com.yh.fridgesoksok.presentation.model.toPresentation
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
                    val userModel = result.data?.let(UserModel::fromLocal)
                    if (userModel == null || !userModel.isValid()) return@onEach fail("채널 응답 데이터가 유효하지 않음")

                    Logger.d("Login", "채널 로그인 성공 $userModel")
                    if (userModel.accountType == "temp") {
                        getUserDefaultFridge(userModel)
                    } else {
                        createUserOnServer(userModel)
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
                    val userModel = result.data?.let(UserModel::fromLocal)
                    if (userModel == null || !userModel.isValid()) return@onEach fail("서버 응답 데이터가 유효하지 않음")

                    Logger.d("Login", "서버 유저 생성 성공 $userModel")
                    val updatedUser = user.withUpdatedUserInfo(
                        newAccess = userModel.accessToken,
                        newRefresh = userModel.refreshToken,
                        newName = userModel.username,
                        newAccountType = userModel.accountType
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
                    val fridgeModel = result.data?.toPresentation()
                    if (fridgeModel == null || !fridgeModel.isValid()) return@onEach fail("기본 냉장고ID 추출 실패")

                    val updatedUser = user.withDefaultFridgeId(fridgeModel.id)
                    saveUser(updatedUser)
                }

                is Resource.Error -> fail("기본 냉장고ID 조회 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun saveUser(user: UserModel) {
        saveUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isUserSaved = result.data ?: false
                    if (!isUserSaved) return@onEach fail("유저 저장 실패")

                    _state.value = LoginState.Success
                    Logger.d("Login", "유저 저장 성공 $user")
                    Logger.i("Login", "로그인 성공")
                }

                is Resource.Error -> fail("유저 저장 실패 (${result.message})")
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