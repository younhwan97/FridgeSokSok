package com.yh.fridgesoksok.presentation.fridge

sealed class FridgeState {
    data object Loading : FridgeState()
    data object Success : FridgeState()
    data object Error : FridgeState()
}