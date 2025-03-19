package com.yh.fridgesoksok.local.impl

import android.content.Context
import android.content.SharedPreferences
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.UserEntity
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    private val context: Context
) : LocalUserDataSource {

    override fun loadUser(): UserEntity {
        val spf: SharedPreferences =
            context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

        val userEntity = UserEntity(
            id = spf.getLong("id", -1L),
            accessToken = spf.getString("accessToken", null),
            refreshToken = spf.getString("refreshToken", null),
            username = spf.getString("username", null),
            accountType = spf.getString("accountType", null),
        )

        return userEntity
    }

    override fun saveUser(userEntity: UserEntity) {
        val spf: SharedPreferences =
            context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val edit = spf.edit()
        edit.putLong("id", userEntity.id)
        edit.putString("accessToken", userEntity.accessToken)
        edit.putString("refreshToken", userEntity.refreshToken)
        edit.putString("username", userEntity.username)
        edit.putString("accountType", userEntity.accountType)
        edit.apply()
    }
}