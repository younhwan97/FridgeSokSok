package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.domain.model.ServerStatus

data class ServerStatusEntity(
    val version: String,
    val status: String,
    val time: String,
    val startTime: String
)

fun ServerStatusEntity.toDomain() =
    ServerStatus(
        version = version,
        status = status,
        time = time,
        startTime = startTime
    )

fun ServerStatus.toEntity() =
    ServerStatusEntity(
        version = version,
        status = status,
        time = time,
        startTime = startTime
    )