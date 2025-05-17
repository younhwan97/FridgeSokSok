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

    companion object {
        private const val TAG = "LocalUserLogger"
        private const val PREF_NAME = "user_info"

        private const val KEY_ID = "id"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_USERNAME = "username"
        private const val KEY_ACCOUNT_TYPE = "accountType"
    }

    private fun getPreferences(): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private inline fun editAndApply(block: SharedPreferences.Editor.() -> Unit) {
        getPreferences().edit().apply {
            block()
            apply()
        }
    }

    private fun log(action: String, type: String, message: Any?) {
        val logMsg = "[$action][$type] $message"
        if (type == "ERROR") Log.e(TAG, logMsg)
        else Log.d(TAG, logMsg)
    }

    override fun loadUser(): UserEntity {
        val action = "loadUser"
        val prefs = getPreferences()

        val user = UserEntity(
            id = prefs.getLong(KEY_ID, -1L),
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null),
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
            username = prefs.getString(KEY_USERNAME, null),
            accountType = prefs.getString(KEY_ACCOUNT_TYPE, null)
        )

        log(action, "OUTPUT", user)
        return user
    }

    override fun saveUser(userEntity: UserEntity) {
        val action = "saveUser"
        log(action, "INPUT", userEntity)

        editAndApply {
            putLong(KEY_ID, userEntity.id)
            putString(KEY_ACCESS_TOKEN, userEntity.accessToken)
            putString(KEY_REFRESH_TOKEN, userEntity.refreshToken)
            putString(KEY_USERNAME, userEntity.username)
            putString(KEY_ACCOUNT_TYPE, userEntity.accountType)
        }
    }

    override fun updateUser(userEntity: UserEntity) {
        val action = "updateUser"
        log(action, "INPUT", userEntity)

        editAndApply {
            if (userEntity.id != -1L) putLong(KEY_ID, userEntity.id)
            userEntity.accessToken?.let { putString(KEY_ACCESS_TOKEN, it) }
            userEntity.refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            userEntity.username?.let { putString(KEY_USERNAME, it) }
            userEntity.accountType?.let { putString(KEY_ACCOUNT_TYPE, it) }
        }
    }

    override fun clearUser() {
        val action = "clearUser"
        log(action, "INPUT", "Clearing user info")

        editAndApply {
            remove(KEY_ID)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USERNAME)
            remove(KEY_ACCOUNT_TYPE)
        }
    }
}