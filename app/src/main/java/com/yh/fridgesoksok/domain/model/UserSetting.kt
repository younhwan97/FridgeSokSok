package com.yh.fridgesoksok.domain.model

data class UserSetting(
    val receiveNotification: Boolean,
    val useAllIngredients: Boolean,
    val autoDeleteExpiredFoods: Boolean,
    val premium: Boolean
)