package com.yh.fridgesoksok.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.usecase.ClearUserUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateAutoDeleteExpiredFoodEnabledUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateExpirationAlarmEnabledUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUseAllIngredientsEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    val clearUserUseCase: ClearUserUseCase,
    private val updateExpirationAlarmEnabledUseCase: UpdateExpirationAlarmEnabledUseCase,
    private val updateAutoDeleteExpiredFoodEnabledUseCase: UpdateAutoDeleteExpiredFoodEnabledUseCase,
    private val updateUseAllIngredientsEnabledUseCase: UpdateUseAllIngredientsEnabledUseCase
) : ViewModel() {

    private val _isExpirationAlarmEnabled = MutableStateFlow(false)
    val isExpirationAlarmEnabled = _isExpirationAlarmEnabled.asStateFlow()

    private val _isAutoDeleteExpiredFoodEnabled = MutableStateFlow(false)
    val isAutoDeleteExpiredFoodEnabled = _isAutoDeleteExpiredFoodEnabled.asStateFlow()

    private val _isUseAllIngredientsEnabled = MutableStateFlow(false)
    val isUseAllIngredientsEnabled = _isUseAllIngredientsEnabled.asStateFlow()

    fun updateExpirationAlarmEnabled(enabled: Boolean) {
        updateExpirationAlarmEnabledUseCase(enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {

                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateAutoDeleteExpiredFoodEnabled(enabled: Boolean) {
        updateAutoDeleteExpiredFoodEnabledUseCase(enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {

                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun updateUseAllIngredientsEnabled(enabled: Boolean) {
        updateUseAllIngredientsEnabledUseCase(enabled).onEach { result ->
            when (result) {
                is Resource.Success -> {

                }

                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
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