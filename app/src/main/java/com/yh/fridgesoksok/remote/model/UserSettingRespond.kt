package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.UserSettingEntity

data class UserSettingRespond(
    val isNotification: Boolean?,
    val useAllIngredients: Boolean?,
    val autoDeleteFoods: Boolean?
)

fun UserSettingRespond.toEntity() =
    UserSettingEntity(
        receiveNotification = isNotification ?: false,
        useAllIngredients = useAllIngredients ?: false,
        autoDeleteExpiredFoods = autoDeleteFoods ?: false
    )