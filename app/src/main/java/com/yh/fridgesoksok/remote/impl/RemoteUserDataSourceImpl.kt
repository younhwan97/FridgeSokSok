package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.model.UserSettingEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
import com.yh.fridgesoksok.remote.model.FcmRequest
import com.yh.fridgesoksok.remote.model.UserCreateRequest
import com.yh.fridgesoksok.remote.model.UserProfileRequest
import com.yh.fridgesoksok.remote.model.UserSettingRequest
import com.yh.fridgesoksok.remote.model.UserTmpCreateRequest
import com.yh.fridgesoksok.remote.model.toEntity
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService,
    private val naverApiService: NaverApiService,
    private val fridgeApiService: FridgeApiService
) : RemoteUserDataSource {

    override suspend fun createUserOnChannel(channel: Channel): UserEntity {
        return try {
            Logger.d("RemoteUserData", "createUserOnChannel INPUT $channel")
            val data = when (channel) {
                Channel.KAKAO -> kakaoApiService.createUserOnKakao()
                Channel.NAVER -> naverApiService.createUserOnNaver()
                Channel.GUEST -> fridgeApiService.createTmpUser(UserTmpCreateRequest(username = "tmp", accountType = "temp")).data
            } ?: throw IllegalStateException("createUserOnChannel data(=null)")

            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "createUserOnChannel 실패", e)
            throw e
        }
    }

    override suspend fun createUserOnServer(user: UserEntity): UserEntity {
        return try {
            Logger.d("RemoteUserData", "createUserOnServer INPUT $user")
            val response = when (user.accountType) {
                Channel.KAKAO.toString() -> fridgeApiService.createUserOnServer(
                    provider = "kakao",
                    userCreateRequest = UserCreateRequest(user.accessToken)
                )

                Channel.NAVER.toString() -> fridgeApiService.createUserOnServer(
                    provider = "naver",
                    userCreateRequest = UserCreateRequest(user.accessToken)
                )

                else -> null
            }
            val data = response?.data ?: throw IllegalStateException("createUserOnServer data(=null)")
            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "createUserOnServer 실패", e)
            throw e
        }
    }

    override suspend fun deleteUser(): Boolean {
        return try {
            Logger.d("RemoteUserData", "deleteUser")
            val response = fridgeApiService.deleteUser()
            val data = response.data ?: throw IllegalStateException("deleteUser data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "deleteUser 실패", e)
            throw e
        }
    }

    override suspend fun validateUserToken(refreshToken: String): Boolean {
        return try {
            Logger.d("RemoteUserData", "validateUserToken INPUT $refreshToken")
            val response = fridgeApiService.validateUserToken()
            val data = response.data ?: throw IllegalStateException("validateUserToken data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "validateUserToken 실패", e)
            throw e
        }
    }

    override suspend fun reissueUserToken(refreshToken: String): TokenEntity {
        return try {
            Logger.d("RemoteUserData", "reissueUserToken INPUT $refreshToken")
            val response = fridgeApiService.reissueUserToken()
            val data = response.data ?: throw IllegalStateException("reissueUserToken data(=null)")
            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "reissueUserToken 실패", e)
            throw e
        }
    }

    override suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity {
        return try {
            Logger.d("RemoteUserData", "getUserDefaultFridge INPUT $accessToken")
            val response = fridgeApiService.getUserDefaultFridge("Bearer $accessToken")
            val data = response.data ?: throw IllegalStateException("getUserDefaultFridge data(=null)")
            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "getUserDefaultFridge 실패", e)
            throw e
        }
    }

    override suspend fun updateReceiveNotification(enabled: Boolean): Boolean {
        return try {
            Logger.d("RemoteUserData", "updateReceiveNotification INPUT $enabled")
            val response = fridgeApiService.updateUserSettings(UserSettingRequest(isNotification = enabled, null, null, null))
            val data = response.data?.isNotification ?: throw IllegalStateException("updateReceiveNotification data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "updateReceiveNotification 실패", e)
            throw e
        }
    }

    override suspend fun updateAutoDeleteExpired(enabled: Boolean): Boolean {
        return try {
            Logger.d("RemoteUserData", "updateAutoDeleteExpired INPUT $enabled")
            val response = fridgeApiService.updateUserSettings(UserSettingRequest(null, null, autoDeleteFoods = enabled, null))
            val data = response.data?.autoDeleteFoods ?: throw IllegalStateException("updateAutoDeleteExpired data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "updateAutoDeleteExpired 실패", e)
            throw e
        }
    }

    override suspend fun updateUseAllIngredients(enabled: Boolean): Boolean {
        return try {
            Logger.d("RemoteUserData", "updateUseAllIngredients INPUT $enabled")
            val response = fridgeApiService.updateUserSettings(UserSettingRequest(null, useAllIngredients = enabled, null, null))
            val data = response.data?.useAllIngredients ?: throw IllegalStateException("updateUseAllIngredients data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "updateUseAllIngredients 실패", e)
            throw e
        }
    }

    override suspend fun getUserSettings(): UserSettingEntity {
        return try {
            Logger.d("RemoteUserData", "getUserSettings")
            val response = fridgeApiService.getUserSettings()
            val data = response.data?.toEntity() ?: throw IllegalStateException("getUserSettings data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "getUserSettings 실패", e)
            throw e
        }
    }

    override suspend fun updateUserFcmToken(fcmToken: String): String {
        return try {
            Logger.d("RemoteUserData", "updateUserFcmToken INPUT $fcmToken")
            val response = fridgeApiService.updateUser(UserProfileRequest(null, null, null, fcmToken, null))
            val data = response.data?.fcmToken ?: throw IllegalStateException("updateUseAllIngredientsEnabled data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "updateUseAllIngredientsEnabled 실패", e)
            throw e
        }
    }

    override suspend fun sendMessage(message: String): String {
        return try {
            Logger.d("RemoteUserData", "sendMessage INPUT $message")
            val response = fridgeApiService.sendFcmTest(FcmRequest(message = message))
            val data = response.data ?: throw IllegalStateException("sendMessage data(=null)")
            data
        } catch (e: Exception) {
            Logger.e("RemoteUserData", "sendMessage 실패", e)
            throw e
        }
    }
}