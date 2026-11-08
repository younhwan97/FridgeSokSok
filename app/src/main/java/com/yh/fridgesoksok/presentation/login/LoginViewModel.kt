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

private const val TAG = "Login"

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

    /**
     * 소셜 로그인 (카카오, 네이버) 또는 게스트 로그인
     */
    fun createUserOnChannel(channel: Channel) {
        Logger.i(TAG, "로그인 시도: $channel")

        createUserOnChannelUseCase(channel).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val userModel = result.data?.let(UserModel::fromLocal)
                    if (userModel == null || !userModel.isValid()) {
                        fail("로그인 정보를 가져올 수 없습니다")
                        return@onEach
                    }

                    Logger.d(TAG, "채널 로그인 성공: ${userModel.accountType}")

                    // 게스트 계정은 서버 생성 단계를 건너뛰고 바로 냉장고 조회
                    if (userModel.accountType == "temp") {
                        getUserDefaultFridge(userModel)
                    } else {
                        createUserOnServer(userModel)
                    }
                }

                is Resource.Error -> fail("${channel.name} 로그인 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 서버에 사용자 정보 생성 및 토큰 발급
     */
    private fun createUserOnServer(user: UserModel) {
        createUserOnServerUseCase(user = user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val userModel = result.data?.let(UserModel::fromLocal)
                    if (userModel == null || !userModel.isValid()) {
                        fail("서버 인증 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "서버 유저 생성 성공: ${userModel.username}")

                    // 서버로부터 받은 토큰 정보로 업데이트
                    val updatedUser = user.withUpdatedUserInfo(
                        newAccess = userModel.accessToken,
                        newRefresh = userModel.refreshToken,
                        newName = userModel.username,
                        newAccountType = userModel.accountType
                    )
                    getUserDefaultFridge(updatedUser)
                }

                is Resource.Error -> fail("서버 인증 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 사용자의 기본 냉장고 정보 조회
     */
    private fun getUserDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val fridgeModel = result.data?.toPresentation()
                    if (fridgeModel == null || !fridgeModel.isValid()) {
                        fail("냉장고 정보를 가져올 수 없습니다")
                        return@onEach
                    }

                    Logger.d(TAG, "기본 냉장고 조회 성공: ${fridgeModel.id}")

                    val updatedUser = user.withDefaultFridgeId(fridgeModel.id)
                    saveUser(updatedUser)
                }

                is Resource.Error -> fail("냉장고 정보 조회 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 로컬에 사용자 정보 저장
     */
    private fun saveUser(user: UserModel) {
        saveUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isUserSaved = result.data ?: false
                    if (!isUserSaved) {
                        fail("사용자 정보 저장 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "유저 로컬 저장 성공: ${user.username}")
                    getApiUsage()
                }

                is Resource.Error -> fail("사용자 정보 저장 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * API 사용량 및 호출 제한 정보 조회
     */
    private fun getApiUsage() {
        getApiUsageUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val usages = result.data
                    if (usages.isNullOrEmpty()) {
                        fail("API 정보를 가져올 수 없습니다")
                        return@onEach
                    }

                    Logger.d(TAG, "API 사용량 정보 조회 성공")
                    updateApiUsage(usages)
                }

                is Resource.Error -> fail("API 정보 조회 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * API 사용량 및 호출 제한 정보를 로컬에 저장
     */
    private fun updateApiUsage(usages: List<ApiUsage>) {
        updateApiUsageUseCase(usages).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val success = result.data ?: false
                    if (!success) {
                        fail("API 정보 저장 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "API 사용량 정보 저장 성공")
                    updateUserFcmToken()
                }

                is Resource.Error -> fail("API 정보 저장 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * FCM 토큰 갱신 (필수)
     */
    private fun updateUserFcmToken() {
        val fcmToken = getLocalUserFcmTokenUseCase()

        if (fcmToken.isBlank()) {
            fail("FCM 토큰이 없습니다")
            return
        }

        updateUserFcmTokenUseCase(fcmToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val updatedToken = result.data
                    if (updatedToken.isNullOrBlank() || updatedToken != fcmToken) {
                        fail("FCM 토큰 갱신 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "FCM 토큰 갱신 성공")
                    success()
                }

                is Resource.Error -> fail("FCM 토큰 갱신 실패: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 로그인 프로세스 성공 처리
     */
    private fun success() {
        Logger.i(TAG, "로그인 성공")
        _state.value = LoginState.Success
    }

    /**
     * 로그인 프로세스 실패 처리
     */
    private fun fail(reason: String) {
        Logger.e(TAG, "$reason → 로그인 실패")
        _state.value = LoginState.Error

        viewModelScope.launch {
            _errorMessage.emit(reason)
        }
    }
}