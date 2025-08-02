package com.yh.fridgesoksok.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.usecase.CreateUserOnChannelUseCase
import com.yh.fridgesoksok.domain.usecase.CreateUserOnServerUseCase
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.SaveUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
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

const val screenName = "Login"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserOnChannelUseCase: CreateUserOnChannelUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val createUserOnServerUseCase: CreateUserOnServerUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase,
    private val getLocalUserFcmTokenUseCase: GetLocalUserFcmTokenUseCase,
    private val updateUserFcmTokenUseCase: UpdateUserFcmTokenUseCase,
    private val getApiUsageUseCase: GetApiUsageUseCase,
    private val updateApiUsageUseCase: UpdateApiUsageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Loading)
    val state = _state.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // (네이버, 카카오 등의) 채널로 부터 유저 생성
    fun createUserOnChannel(channel: Channel) {
        Logger.i(screenName, "로그인 시도 $channel")

        createUserOnChannelUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val userModel = result.data?.let(UserModel::fromLocal)
                    if (userModel == null || !userModel.isValid()) {
                        fail("채널 응답 데이터가 유효하지 않음")
                        return@onEach
                    }

                    Logger.d(screenName, "채널 로그인 성공 $userModel")
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
                    if (userModel == null || !userModel.isValid()) {
                        fail("서버 응답 데이터가 유효하지 않음")
                        return@onEach
                    }

                    Logger.d(screenName, "서버 유저 생성 성공 $userModel")
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

    // 유저 기본냉장고 추출
    private fun getUserDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val fridgeModel = result.data?.toPresentation()
                    if (fridgeModel == null || !fridgeModel.isValid()) {
                        fail("기본 냉장고ID 추출 실패")
                        return@onEach
                    }

                    val updatedUser = user.withDefaultFridgeId(fridgeModel.id)
                    saveUser(updatedUser)
                }

                is Resource.Error -> fail("기본 냉장고ID 조회 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 저장
    private fun saveUser(user: UserModel) {
        saveUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isUserSaved = result.data ?: false
                    if (!isUserSaved) {
                        fail("유저 저장 실패")
                        return@onEach
                    }

                    Logger.d(screenName, "유저 저장 성공 $user")
                    getApiUsage()
                }

                is Resource.Error -> fail("유저 저장 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // API 사용량 및 호출제한 확인
    private fun getApiUsage() {
        getApiUsageUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val usages = result.data
                    if (usages.isNullOrEmpty()) {
                        fail("유저 API 정보 확인 실패")
                        return@onEach
                    }

                    Logger.d(screenName, "API 정보 확인 성공")
                    updateApiUsage(usages)
                }

                is Resource.Error -> fail("API 정보 확인 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // API 사용량 및 호출제한 정보 저장
    private fun updateApiUsage(usages: List<ApiUsage>) {
        updateApiUsageUseCase(usages).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val success = result.data ?: false
                    if (!success) {
                        fail("유저 API 정보 저장 실패")
                        return@onEach
                    }

                    Logger.d(screenName, "API 정보 저장 성공")
                    updateUserFcmToken()
                }

                is Resource.Error -> fail("API 정보 저장 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // FCM 토큰 갱신
    private fun updateUserFcmToken() {
        val fcmToken = getLocalUserFcmTokenUseCase()

        if (fcmToken.isNotBlank()) {
            updateUserFcmTokenUseCase(fcmToken).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val isUserUpdated = result.data == fcmToken
                        if (!isUserUpdated) {
                            fail("FCM 토큰 갱신 실패")
                            return@onEach
                        }

                        Logger.d(screenName, "FCM 토큰 갱신 성공")
                        Logger.i(screenName, "로그인 성공")
                        _state.value = LoginState.Success
                    }

                    is Resource.Error -> fail("FCM 토큰 갱신 실패 ${result.message}")
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        } else {
            Logger.d(screenName, "FCM 토큰 갱신 성공")
            Logger.i(screenName, "로그인 성공")
            _state.value = LoginState.Success
        }
    }

    private fun fail(reason: String) {
        Logger.e(screenName, "$reason → 로그인 실패")
        _state.value = LoginState.Error

        viewModelScope.launch {
            _errorMessage.emit("로그인 실패")
        }
    }
}