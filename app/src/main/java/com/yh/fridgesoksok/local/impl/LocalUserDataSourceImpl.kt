package com.yh.fridgesoksok.local.impl

import android.content.Context
import android.content.SharedPreferences
import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.model.ApiUsageEntity
import com.yh.fridgesoksok.data.model.UserEntity
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    private val context: Context
) : LocalUserDataSource {

    companion object {
        private const val PREF_NAME = "user_info"

        private const val KEY_ID = "id"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_USERNAME = "username"
        private const val KEY_ACCOUNT_TYPE = "accountType"
        private const val KEY_DEFAULT_FRIDGE_ID = "defaultFridgeId"

        private const val KEY_FCM_TOKEN = "fcmToken"
    }

    private fun getPreferences(): SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private inline fun editAndCommit(block: SharedPreferences.Editor.() -> Unit): Boolean {
        return getPreferences().edit().apply {
            block()
        }.commit()
    }

    override fun loadUser(): UserEntity {
        val prefs = getPreferences()
        return UserEntity(
            id = prefs.getString(KEY_ID, null).orEmpty(),
            accessToken = prefs.getString(KEY_ACCESS_TOKEN, null).orEmpty(),
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null).orEmpty(),
            username = prefs.getString(KEY_USERNAME, null).orEmpty(),
            accountType = prefs.getString(KEY_ACCOUNT_TYPE, null).orEmpty(),
            defaultFridgeId = prefs.getString(KEY_DEFAULT_FRIDGE_ID, null).orEmpty()
        )
    }

    override suspend fun saveUser(userEntity: UserEntity): Boolean =
        putUser(userEntity)

    override suspend fun updateUser(userEntity: UserEntity): Boolean =
        putUser(userEntity)

    private fun putUser(userEntity: UserEntity): Boolean =
        editAndCommit {
            putString(KEY_ID, userEntity.id)
            putString(KEY_ACCESS_TOKEN, userEntity.accessToken)
            putString(KEY_REFRESH_TOKEN, userEntity.refreshToken)
            putString(KEY_USERNAME, userEntity.username)
            putString(KEY_ACCOUNT_TYPE, userEntity.accountType)
            putString(KEY_DEFAULT_FRIDGE_ID, userEntity.defaultFridgeId)
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

    override fun updateTokens(accessToken: String, refreshToken: String): Boolean =
        editAndCommit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }

    override suspend fun updateUserFcmToken(fcmToken: String): Boolean =
        editAndCommit {
            putString(KEY_FCM_TOKEN, fcmToken)
        }

    override fun getUserFcmToken(): String {
        val prefs = getPreferences()
        return prefs.getString(KEY_FCM_TOKEN, null).orEmpty()
    }

    override suspend fun updateApiUsage(apiUsages: List<ApiUsageEntity>): Boolean {
        return editAndCommit {
            apiUsages.forEach { usage ->
                putInt("${usage.feature}_MAX", usage.max)
                putInt("${usage.feature}_USED", usage.used)
            }
        }
    }

    override fun getApiUsage(feature: String): ApiUsageEntity {
        val prefs = getPreferences()
        return ApiUsageEntity(
            feature = feature,
            max = prefs.getInt("${feature}_MAX", 0),
            used = prefs.getInt("${feature}_USED", 0),
        )
    }
}