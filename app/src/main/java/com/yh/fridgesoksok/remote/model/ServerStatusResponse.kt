package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.ServerStatusEntity

data class ServerStatusResponse(
    val serverStartTimeKST: String,
    val version: String,
    val status: String,
    val timestamp: String
)

fun ServerStatusResponse.toEntity() =
    ServerStatusEntity(
        version = version,
        status = status,
        time = timestamp,
        startTime = serverStartTimeKST
    )