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

    private inline fun editAndCommit(block: SharedPreferences.Editor.() -> Unit): Boolean {
        val success = getPreferences().edit().apply {
            block()
        }.commit()

        return success
    }

    override fun loadUser(): UserEntity {
        val prefs = getPreferences()

        val user = UserEntity(
            id = prefs.getLong(KEY_ID, -1L),
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null),
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
            username = prefs.getString(KEY_USERNAME, null),
            accountType = prefs.getString(KEY_ACCOUNT_TYPE, null)
        )

        return user
    }

    override suspend fun saveUser(userEntity: UserEntity): Boolean {
        return editAndCommit {
            putLong(KEY_ID, userEntity.id)
            putString(KEY_ACCESS_TOKEN, userEntity.accessToken)
            putString(KEY_REFRESH_TOKEN, userEntity.refreshToken)
            putString(KEY_USERNAME, userEntity.username)
            putString(KEY_ACCOUNT_TYPE, userEntity.accountType)
        }
    }

    override suspend fun updateUser(userEntity: UserEntity): Boolean {
        return editAndCommit {
            if (userEntity.id != -1L) putLong(KEY_ID, userEntity.id)
            userEntity.accessToken?.let { putString(KEY_ACCESS_TOKEN, it) }
            userEntity.refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            userEntity.username?.let { putString(KEY_USERNAME, it) }
            userEntity.accountType?.let { putString(KEY_ACCOUNT_TYPE, it) }
        }
    }

    override suspend fun clearUser(): Boolean =
        editAndCommit {
            remove(KEY_ID)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USERNAME)
            remove(KEY_ACCOUNT_TYPE)
        }
}