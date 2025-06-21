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
        private const val KEY_DEFAULT_FRIDGE_ID = "defaultFridgeId"

        private const val KEY_FCM_TOKEN = "fcmToken"
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
            id = prefs.getString(KEY_ID, null).orEmpty(),
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null).orEmpty(),
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null).orEmpty(),
            username = prefs.getString(KEY_USERNAME, null).orEmpty(),
            accountType = prefs.getString(KEY_ACCOUNT_TYPE, null).orEmpty(),
            defaultFridgeId = prefs.getString(KEY_DEFAULT_FRIDGE_ID, null).orEmpty()
        )

        return user
    }

    override suspend fun saveUser(userEntity: UserEntity): Boolean {
        return editAndCommit {
            putString(KEY_ID, userEntity.id)
            putString(KEY_ACCESS_TOKEN, userEntity.accessToken)
            putString(KEY_REFRESH_TOKEN, userEntity.refreshToken)
            putString(KEY_USERNAME, userEntity.username)
            putString(KEY_ACCOUNT_TYPE, userEntity.accountType)
            putString(KEY_DEFAULT_FRIDGE_ID, userEntity.defaultFridgeId)
        }
    }

    override suspend fun updateUser(userEntity: UserEntity): Boolean {
        return editAndCommit {
            putString(KEY_ID, userEntity.id)
            putString(KEY_ACCESS_TOKEN, userEntity.accessToken)
            putString(KEY_REFRESH_TOKEN, userEntity.refreshToken)
            putString(KEY_USERNAME, userEntity.username)
            putString(KEY_ACCOUNT_TYPE, userEntity.accountType)
            putString(KEY_DEFAULT_FRIDGE_ID, userEntity.defaultFridgeId)
        }
    }

    override suspend fun clearUser(): Boolean =
        editAndCommit {
            remove(KEY_ID)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USERNAME)
            remove(KEY_ACCOUNT_TYPE)
            remove(KEY_DEFAULT_FRIDGE_ID)
        }

    override suspend fun updateUserFcmToken(fcmToken: String): Boolean {
        return editAndCommit {
            putString(KEY_FCM_TOKEN, fcmToken)
        }
    }

    override fun getUserFcmToken(): String {
        val prefs = getPreferences()

        val pcmToken = prefs.getString(KEY_FCM_TOKEN, null).orEmpty()

        return pcmToken
    }
}