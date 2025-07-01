package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.UserSetting

data class UserSettingEntity(
    val receiveNotification: Boolean,
    val useAllIngredients: Boolean,
    val autoDeleteExpiredFoods: Boolean,
    val premium: Boolean
)

fun UserSetting.toEntity(): UserSettingEntity =
    UserSettingEntity(
        receiveNotification = receiveNotification,
        useAllIngredients = useAllIngredients,
        autoDeleteExpiredFoods = autoDeleteExpiredFoods,
        premium = premium
    )

fun UserSettingEntity.toDomain(): UserSetting =
    UserSetting(
        receiveNotification = receiveNotification,
        useAllIngredients = useAllIngredients,
        autoDeleteExpiredFoods = autoDeleteExpiredFoods,
        premium = premium
    )