package com.yh.fridgesoksok.data.local

import com.yh.fridgesoksok.data.model.UserEntity

interface LocalUserDataSource {

    fun loadUser(): UserEntity

    fun saveUser(userEntity: UserEntity): Unit
}