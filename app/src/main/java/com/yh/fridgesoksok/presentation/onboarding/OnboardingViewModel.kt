package com.yh.fridgesoksok.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Api
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.usecase.GetApiUsageFromLocalUseCase
import com.yh.fridgesoksok.domain.usecase.GetApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.GetLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserDefaultFridgeUseCase
import com.yh.fridgesoksok.domain.usecase.LoadUserUseCase
import com.yh.fridgesoksok.domain.usecase.ReissueUserTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateApiUsageUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserUseCase
import com.yh.fridgesoksok.domain.usecase.ValidateUserTokenUseCase
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val screenName = "Onboarding"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val validateUserTokenUseCase: ValidateUserTokenUseCase,
    private val reissueUserTokenUseCase: ReissueUserTokenUseCase,
    private val getUserDefaultFridgeUseCase: GetUserDefaultFridgeUseCase,
    private val getLocalUserFcmTokenUseCase: GetLocalUserFcmTokenUseCase,
    private val updateUserFcmTokenUseCase: UpdateUserFcmTokenUseCase,
    private val getApiUsageUseCase: GetApiUsageUseCase,
    private val updateApiUsageUseCase: UpdateApiUsageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val state = _state.asStateFlow()

    init {
        /* 온보딩 프로세스
            1. (로컬) 유저정보 로드
            2. (서버) 토큰 유효성 확인
            3. (서버) 토큰 갱신
            4. (서버) 유저 기본냉장고 ID 추출
            5. (로컬) 유저정보 업데이트
            6. (서버) API 사용량 및 제한 확인
            7. (로컬) API 사용량 및 제한 정보 로컬 저장
            8. (로컬) 로컬 FCM 토큰 확인
            9. (서버) FCM 토큰 갱신 */
        startOnboarding()
    }

    private fun startOnboarding() {
        Logger.i(screenName, "온보딩 시작")
        val user = UserModel.fromLocal(loadUserUseCase())
        if (!user.isValid()) return fail("토큰정보 없음")
        validateToken(user)
    }

    // 유저 리프레쉬 토큰 유효성 확인
    private fun validateToken(user: UserModel) {
        validateUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isValid = result.data ?: false
                    if (!isValid) {
                        fail("리프레쉬 토큰 무효")
                        return@onEach
                    }

                    Logger.d(screenName, "리프레쉬 토큰 유효")
                    reissueToken(user)
                }

                is Resource.Error -> fail("리프레쉬 토큰 검증오류 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 (엑세스+리프레쉬) 토큰 갱신
    private fun reissueToken(user: UserModel) {
        reissueUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val token = result.data?.toPresentation()
                    if (token == null || !token.isValid()) {
                        fail("토큰 갱신 실패")
                        return@onEach
                    }

                    Logger.d(screenName, "토큰 갱신 성공")
                    val updatedUser = user.withReissuedToken(token.accessToken, token.refreshToken)
                    getDefaultFridge(updatedUser)
                }

                is Resource.Error -> fail("토큰 갱신 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 기본냉장고 추출
    private fun getDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val fridge = result.data?.toPresentation()
                    if (fridge == null || !fridge.isValid()) {
                        fail("기본 냉장고ID 추출 실패")
                        return@onEach
                    }

                    Logger.d(screenName, "기본 냉장고ID 추출 성공")
                    val updatedUser = user.withDefaultFridgeId(fridge.id)
                    updateUser(updatedUser)
                }

                is Resource.Error -> fail("기본 냉장고ID 추출 실패 (${result.message})")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    // 유저 저장
    private fun updateUser(user: UserModel) {
        updateUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val success = result.data ?: false
                    if (!success) {
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
                        val success = result.data == fcmToken
                        if (!success) {
                            fail("FCM 토큰 갱신 실패")
                            return@onEach
                        }

                        viewModelScope.launch {
                            _state.value = OnboardingState.Success
                        }

                        Logger.d(screenName, "FCM 토큰 갱신 성공")
                        Logger.i(screenName, "온보딩 완료")
                    }

                    is Resource.Error -> fail("FCM 토큰 갱신 실패 (${result.message})")
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        } else {
            viewModelScope.launch {
                _state.value = OnboardingState.Success
            }

            Logger.i(screenName, "온보딩 완료")
        }
    }

    private fun fail(reason: String) {
        Logger.e(screenName, "$reason → 온보딩 실패")
        _state.value = OnboardingState.Error
    }
}