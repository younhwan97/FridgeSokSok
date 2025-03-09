package com.yh.fridgesoksok.data.remote

interface RemoteUserDataSource {

    suspend fun login(): String
}