package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserSettingEntity

data class UserSettingResponse(
    val isNotification: Boolean?,
    val useAllIngredients: Boolean?,
    val autoDeleteFoods: Boolean?
)

fun UserSettingResponse.toEntity() =
    UserSettingEntity(
        receiveNotification = isNotification ?: false,
        useAllIngredients = useAllIngredients ?: false,
        autoDeleteExpiredFoods = autoDeleteFoods ?: false
    )