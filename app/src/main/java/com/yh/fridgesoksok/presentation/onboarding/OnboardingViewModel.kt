package com.yh.fridgesoksok.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.ApiUsage
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
import com.yh.fridgesoksok.presentation.model.UserModel
import com.yh.fridgesoksok.presentation.model.toDomain
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "Onboarding"

/**
 * 온보딩 프로세스의 각 단계를 나타내는 sealed class
 */
private sealed class OnboardingStep {
    data object CheckServerStatus : OnboardingStep()
    data class ValidateToken(val user: UserModel) : OnboardingStep()
    data class ReissueToken(val user: UserModel) : OnboardingStep()
    data class GetDefaultFridge(val user: UserModel) : OnboardingStep()
    data class UpdateUser(val user: UserModel) : OnboardingStep()
    data object GetApiUsage : OnboardingStep()
    data class UpdateApiUsage(val usages: List<ApiUsage>) : OnboardingStep()
    data object UpdateFcmToken : OnboardingStep()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getServerStatusUseCase: GetServerStatusUseCase,
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

    private val _isServerConnected = MutableStateFlow(true)
    val isServerConnected = _isServerConnected.asStateFlow()

    init {
        startOnboarding()
    }

    /**
     * 온보딩 프로세스 시작
     */
    private fun startOnboarding() {
        Logger.i(TAG, "온보딩 시작")
        _state.value = OnboardingState.Loading
        _isServerConnected.value = true
        proceedToNextStep(OnboardingStep.CheckServerStatus)
    }

    /**
     * 온보딩 재시도 (서버 연결 실패 후)
     */
    fun retryOnboarding() {
        Logger.i(TAG, "온보딩 재시도")
        startOnboarding()
    }

    /**
     * 온보딩 프로세스의 다음 단계로 진행
     * 각 단계에 따라 적절한 함수를 호출
     */
    private fun proceedToNextStep(step: OnboardingStep) {
        when (step) {
            is OnboardingStep.CheckServerStatus -> checkServerStatus()
            is OnboardingStep.ValidateToken -> validateToken(step.user)
            is OnboardingStep.ReissueToken -> reissueToken(step.user)
            is OnboardingStep.GetDefaultFridge -> getDefaultFridge(step.user)
            is OnboardingStep.UpdateUser -> updateUser(step.user)
            is OnboardingStep.GetApiUsage -> getApiUsage()
            is OnboardingStep.UpdateApiUsage -> updateApiUsage(step.usages)
            is OnboardingStep.UpdateFcmToken -> updateUserFcmToken()
        }
    }

    /**
     * 온보딩 프로세스 실패 처리
     */
    private fun fail(reason: String) {
        Logger.e(TAG, "$reason → 온보딩 실패")
        _state.value = OnboardingState.Error
    }

    /**
     * 온보딩 프로세스 성공 처리
     */
    private fun complete() {
        Logger.i(TAG, "온보딩 완료")
        _state.value = OnboardingState.Success
    }

    /**
     * 1단계: 서버 상태 확인
     */
    private fun checkServerStatus() {
        getServerStatusUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.status != "UP") {
                        _isServerConnected.value = false
                        fail("서버 상태 비정상")
                        return@onEach
                    }

                    Logger.d(TAG, "서버 상태 정상")

                    // 로컬에서 사용자 정보 로드
                    val user = UserModel.fromLocal(loadUserUseCase())
                    if (!user.isValid()) {
                        fail("저장된 토큰 정보 없음")
                        return@onEach
                    }

                    proceedToNextStep(OnboardingStep.ValidateToken(user))
                }

                is Resource.Error -> {
                    _isServerConnected.value = false
                    fail("서버 연결 실패: ${result.message}")
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 2단계: 리프레시 토큰 유효성 확인
     */
    private fun validateToken(user: UserModel) {
        validateUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != true) {
                        fail("리프레시 토큰 무효")
                        return@onEach
                    }

                    Logger.d(TAG, "리프레시 토큰 유효")
                    proceedToNextStep(OnboardingStep.ReissueToken(user))
                }

                is Resource.Error -> fail("토큰 검증 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 3단계: 액세스 토큰 및 리프레시 토큰 재발급
     */
    private fun reissueToken(user: UserModel) {
        reissueUserTokenUseCase(user.refreshToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val token = result.data?.toPresentation()
                    if (token == null || !token.isValid()) {
                        fail("토큰 재발급 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "토큰 재발급 성공")
                    val updatedUser = user.withReissuedToken(token.accessToken, token.refreshToken)
                    proceedToNextStep(OnboardingStep.GetDefaultFridge(updatedUser))
                }

                is Resource.Error -> fail("토큰 재발급 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 4단계: 사용자 기본 냉장고 ID 조회
     */
    private fun getDefaultFridge(user: UserModel) {
        getUserDefaultFridgeUseCase(user.accessToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val fridge = result.data?.toPresentation()
                    if (fridge == null || !fridge.isValid()) {
                        fail("기본 냉장고 조회 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "기본 냉장고 ID: ${fridge.id}")
                    val updatedUser = user.withDefaultFridgeId(fridge.id)
                    proceedToNextStep(OnboardingStep.UpdateUser(updatedUser))
                }

                is Resource.Error -> fail("기본 냉장고 조회 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 5단계: 사용자 정보 로컬 저장
     */
    private fun updateUser(user: UserModel) {
        updateUserUseCase(user.toDomain()).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != true) {
                        fail("사용자 정보 저장 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "사용자 정보 저장 완료")
                    proceedToNextStep(OnboardingStep.GetApiUsage)
                }

                is Resource.Error -> fail("사용자 정보 저장 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 6단계: API 사용량 정보 조회
     */
    private fun getApiUsage() {
        getApiUsageUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val usages = result.data
                    if (usages.isNullOrEmpty()) {
                        fail("API 사용량 정보 조회 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "API 사용량 정보 조회 완료")
                    proceedToNextStep(OnboardingStep.UpdateApiUsage(usages))
                }

                is Resource.Error -> fail("API 사용량 조회 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 7단계: API 사용량 정보 로컬 저장
     */
    private fun updateApiUsage(usages: List<ApiUsage>) {
        updateApiUsageUseCase(usages).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != true) {
                        fail("API 사용량 정보 저장 실패")
                        return@onEach
                    }

                    Logger.d(TAG, "API 사용량 정보 저장 완료")
                    proceedToNextStep(OnboardingStep.UpdateFcmToken)
                }

                is Resource.Error -> fail("API 사용량 저장 오류: ${result.message}")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 8단계(선택적): FCM 토큰 서버 동기화
     * FCM 토큰이 없거나 업데이트에 실패해도 온보딩은 성공으로 처리
     */
    private fun updateUserFcmToken() {
        val fcmToken = getLocalUserFcmTokenUseCase()

        if (fcmToken.isBlank()) {
            Logger.d(TAG, "FCM 토큰 없음 (선택적 단계 스킵)")
            complete()
            return
        }

        updateUserFcmTokenUseCase(fcmToken).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Logger.d(TAG, "FCM 토큰 동기화 완료")
                    complete()
                }

                is Resource.Error -> {
                    Logger.d(TAG, "FCM 토큰 동기화 실패 (선택적 단계): ${result.message}")
                    complete()
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }
}