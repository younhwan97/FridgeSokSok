package com.yh.fridgesoksok.local.impl

import android.content.Context
import android.content.SharedPreferences
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    //private val context: Context
) : LocalUserDataSource {

    override fun getUserToken(): String {
        //val spf : SharedPreferences = context.getSharedPreferences("user_token", Context.MODE_PRIVATE)

        return "ttttttt"
    }
}