package com.yh.fridgesoksok.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.ClearUserUseCase
import com.yh.fridgesoksok.domain.usecase.GetUserSettingUseCase
import com.yh.fridgesoksok.domain.usecase.SendMessageUseCase
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
    val clearUserUseCase: ClearUserUseCase,
    private val getUserSettingUseCase: GetUserSettingUseCase,
    private val updateReceiveNotificationUseCase: UpdateReceiveNotificationUseCase,
    private val updateAutoDeleteExpiredUseCase: UpdateAutoDeleteExpiredUseCase,
    private val updateUseAllIngredientsUseCase: UpdateUseAllIngredientsUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _userSetting = MutableStateFlow<UserSettingModel?>(null)
    val userSetting = _userSetting.asStateFlow()

    init {
        getUserSetting()
    }

    fun getUserSetting() {
        getUserSettingUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val userSetting = result.data?.toPresentation()
                    if (userSetting != null) {
                        _userSetting.value = userSetting
                    }
                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateReceiveNotification(enabled: Boolean?) {
        if (enabled != null) {
            updateReceiveNotificationUseCase(!enabled).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data
                        if (data != null){
                            _userSetting.value = _userSetting.value?.copy(receiveNotification = data)
                        }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateAutoDeleteExpired(enabled: Boolean?) {
        if (enabled != null) {
            updateAutoDeleteExpiredUseCase(!enabled).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data
                        if (data != null){
                            _userSetting.value = _userSetting.value?.copy(autoDeleteExpiredFoods = data)
                        }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        }
    }

    fun updateUseAllIngredients(enabled: Boolean?) {
        if (enabled != null) {
            updateUseAllIngredientsUseCase(!enabled).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val data = result.data
                        if (data != null){
                            _userSetting.value = _userSetting.value?.copy(useAllIngredients = data)
                        }
                    }

                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }.launchIn(viewModelScope)
        }
    }

    fun clearUser() {
        clearUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {

                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }
}

//        sendMessageUseCase("test").onEach { result ->
//            when (result) {
//                is Resource.Success -> {
//                    Log.d("test444", result.toString())
//                }
//
//                is Resource.Error -> Unit
//                is Resource.Loading -> Unit
//            }
//        }.launchIn(viewModelScope)