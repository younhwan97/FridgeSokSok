package com.yh.fridgesoksok.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiMode = MutableStateFlow(HomeUiMode.DEFAULT)
    val uiMode = _uiMode.asStateFlow()

    fun updateToDefaultUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }

    fun updateToRecipeUiMode() {
        _uiMode.value = HomeUiMode.RECIPE_SELECT
    }

    fun resetUiMode() {
        _uiMode.value = HomeUiMode.DEFAULT
    }
}