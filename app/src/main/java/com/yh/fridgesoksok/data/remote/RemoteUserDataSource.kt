package com.yh.fridgesoksok.data.remote

import com.yh.fridgesoksok.common.LoginMethod

interface RemoteUserDataSource {

    suspend fun login(loginMethod: LoginMethod): String
}