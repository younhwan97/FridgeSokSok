package com.yh.fridgesoksok.remote.model

data class UserSettingRequest(
    val isNotification: Boolean?,
    val useAllIngredients: Boolean?
)