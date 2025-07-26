package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.ApiUsage

data class ApiUsageEntity(
    val feature: String,
    val max: Int,
    val used: Int
)

fun ApiUsage.toEntity() =
    ApiUsageEntity(
        feature = feature,
        max = max,
        used = used
    )

fun ApiUsageEntity.toDomain() =
    ApiUsage(
        feature = feature,
        max = max,
        used = used
    )