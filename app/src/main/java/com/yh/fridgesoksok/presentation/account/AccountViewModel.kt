package com.yh.fridgesoksok.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.ClearUserUseCase
import com.yh.fridgesoksok.domain.usecase.DeleteUserUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserSettingUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateAutoDeleteExpiredUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateReceiveNotificationUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUseAllIngredientsUseCase
import com.yh.fridgesoksok.presentation.model.UserSettingModel
import com.yh.fridgesoksok.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val clearUserUseCase: ClearUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getUserSettingUseCase: GetUserSettingUseCase,
    private val updateReceiveNotificationUseCase: UpdateReceiveNotificationUseCase,
    private val updateAutoDeleteExpiredUseCase: UpdateAutoDeleteExpiredUseCase,
    private val updateUseAllIngredientsUseCase: UpdateUseAllIngredientsUseCase,
    // private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _userSetting = MutableStateFlow<UserSettingModel?>(null)
    val userSetting = _userSetting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _successClearUser = MutableStateFlow<Boolean?>(null)
    val successClearUser = _successClearUser.asStateFlow()

    private val _successDeleteUser = MutableStateFlow<Boolean?>(null)
    val successDeleteUser = _successDeleteUser.asStateFlow()

    init {
        getUserSetting()
    }

    private fun getUserSetting() {
        getUserSettingUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val userSetting = result.data?.toPresentation()
                    if (userSetting != null) {
                        _userSetting.value = userSetting
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateReceiveNotification(enabled: Boolean?) {
        if (enabled == null) return

        updateReceiveNotificationUseCase(!enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _userSetting.value = _userSetting.value?.copy(receiveNotification = data)
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateAutoDeleteExpired(enabled: Boolean?) {
        if (enabled == null) return

        updateAutoDeleteExpiredUseCase(!enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _userSetting.value = _userSetting.value?.copy(autoDeleteExpiredFoods = data)
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateUseAllIngredients(enabled: Boolean?) {
        if (enabled == null) return

        updateUseAllIngredientsUseCase(!enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _userSetting.value = _userSetting.value?.copy(useAllIngredients = data)
                    }
                }

                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun clearUser() {
        clearUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data == true) {
                        _successClearUser.value = true
                    } else {
                        _successClearUser.value = false
                        _errorMessage.value = "로그아웃에 실패했어요 :("
                    }
                }

                is Resource.Error -> {
                    _successClearUser.value = false
                    _errorMessage.value = "로그아웃에 실패했어요 :("
                }

                is Resource.Loading -> {
                    _successClearUser.value = null
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteUser() {
        deleteUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    clearUserUseCase().onEach {
                        // 로그아웃 성공여부와 상관없이, 서버에서 회원탈퇴 처리가 완료됐다면 true 셋팅
                        _successDeleteUser.value = true
                    }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    _successDeleteUser.value = false
                    _errorMessage.value = "회월탈퇴에 실패했어요 :("
                }

                is Resource.Loading -> {
                    _successDeleteUser.value = null
                }

            }
        }.launchIn(viewModelScope)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}