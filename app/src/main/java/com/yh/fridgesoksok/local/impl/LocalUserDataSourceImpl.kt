package com.yh.fridgesoksok.local.impl

import android.content.Context
import android.content.SharedPreferences
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    private val context: Context
) : LocalUserDataSource {

    override fun getUserToken(): String? {
        val spf: SharedPreferences =
            context.getSharedPreferences("user_token", Context.MODE_PRIVATE)
        val value = spf.getString("token", "")
        return value
    }

    override fun setUserToken(token: String) {
        val spf: SharedPreferences =
            context.getSharedPreferences("user_token", Context.MODE_PRIVATE)
        val edit = spf.edit()
        edit.putString("token", token)
        edit.apply()
    }
}