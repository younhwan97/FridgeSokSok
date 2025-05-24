package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
import com.yh.fridgesoksok.remote.model.UserCreateRequest
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
                    userCreateRequest = UserCreateRequest(token = user.accessToken, username = user.id)
                )

                Channel.NAVER.toString() -> fridgeApiService.createUserOnServer(
                    provider = "naver",
                    userCreateRequest = UserCreateRequest(token = user.accessToken, username = user.id)
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
}