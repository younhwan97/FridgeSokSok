package com.yh.fridgesoksok.local.impl

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.UserEntity
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    private val context: Context
) : LocalUserDataSource {

    override fun loadUser(): UserEntity {
        val spf: SharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

        val userEntity = UserEntity(
            id = spf.getLong("id", -1L),
            accessToken = spf.getString("accessToken", null),
            refreshToken = spf.getString("refreshToken", null),
            username = spf.getString("username", null),
            accountType = spf.getString("accountType", null),
        )
        Log.d("OUTPUT(loadUser): ", userEntity.toString())
        return userEntity
    }

    override fun saveUser(userEntity: UserEntity) {
        Log.d("INPUT(saveUser): ", userEntity.toString())

        val spf: SharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val edit = spf.edit()

        if (userEntity.id != -1L) { edit.putLong("id", userEntity.id) }
        userEntity.accessToken?.let { edit.putString("accessToken", it) }
        userEntity.refreshToken?.let { edit.putString("refreshToken", it) }
        userEntity.username?.let { edit.putString("username", it) }
        userEntity.accountType?.let { edit.putString("accountType", it) }

        edit.apply()
    }

    override fun clearUser() {
        val spf: SharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val edit = spf.edit()

        edit.putLong("id", -1)
        edit.putString("accessToken", "")
        edit.putString("refreshToken", "")
        edit.putString("username", "")
        edit.putString("accountType", "")

        edit.apply()
    }
}