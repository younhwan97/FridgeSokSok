package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.UserSetting

data class UserSettingModel(
    val receiveNotification: Boolean,
    val useAllIngredients: Boolean,
    val autoDeleteExpiredFoods: Boolean
)

fun UserSetting.toPresentation() =
    UserSettingModel(
        receiveNotification = receiveNotification,
        useAllIngredients = useAllIngredients,
        autoDeleteExpiredFoods = autoDeleteExpiredFoods
    )