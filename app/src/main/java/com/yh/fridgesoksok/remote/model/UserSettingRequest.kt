package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserSettingEntity

data class UserSettingRequest(
    val isNotification: Boolean?,
    val useAllIngredients: Boolean?,
    val autoDeleteFoods: Boolean?
)

fun UserSettingEntity.toRequest() =
    UserSettingRequest(
        isNotification = receiveNotification,
        useAllIngredients = useAllIngredients,
        autoDeleteFoods = autoDeleteExpiredFoods
    )