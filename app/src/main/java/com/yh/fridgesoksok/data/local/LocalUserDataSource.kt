package com.yh.fridgesoksok.data.local

interface LocalUserDataSource {

    fun getUserToken(): String?

    fun setUserToken(token: String): Unit
}