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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _successServerRequest = MutableSharedFlow<Unit>()
    val successServerRequest = _successServerRequest.asSharedFlow()

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

                is Resource.Error -> _userSetting.value = null
                is Resource.Loading -> Unit
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
                    if (result.data == true) {
                        _successServerRequest.emit(Unit)
                    } else {
                        _errorMessage.emit("로그아웃에 실패했어요 :(")
                    }
                }

                is Resource.Error -> _errorMessage.emit("로그아웃에 실패했어요 :(")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun deleteUser() {
        deleteUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data == true) {
                        clearUserUseCase().onEach {
                            // 로그아웃 성공여부와 상관없이, 서버에서 회원탈퇴 처리가 완료됐다면 true 셋팅
                            _successServerRequest.emit(Unit)
                        }.launchIn(viewModelScope)
                    } else {
                        _errorMessage.emit("회원탈퇴에 실패했어요 :(")
                    }
                }

                is Resource.Error -> _errorMessage.emit("회월탈퇴에 실패했어요 :(")
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }
}