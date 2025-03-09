package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.Channel

interface RemoteUserDataSource {

    suspend fun createUserToken(channel: Channel): String
}