package com.yh.fridgesoksok.remote.impl

import android.util.Log
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService,
    private val fridgeApiService: FridgeApiService
) : RemoteUserDataSource {

    override suspend fun createUserToken(channel: Channel): UserEntity {

        val userResponse: UserResponse = when (channel) {
            Channel.KAKAO -> kakaoApiService.createKakaoToken()
            Channel.NAVER -> TODO()
            Channel.GOOGLE -> TODO()
            Channel.FACEBOOK -> TODO()
            else -> UserResponse(id = -1L, null, null, null, null)
        }

        return userResponse.toData()
    }

    override suspend fun createUser(token: String, username: String): UserEntity {

        val userRequest = UserRequest(token = token, username = username)

        try {
            val response = fridgeApiService.createUser(userRequest = userRequest)
            return response.userResponse.toData()
        } catch (e: Exception) {
            Log.e("API Error", "Request failed: ${e.message}")
            return UserEntity(id = -1L, null, null, null, null)
        }
    }

    override suspend fun validateUserToken(refreshToken: String): Boolean {
        try {
            val response = fridgeApiService.validateUserToken()
            if (response.data is Boolean) {
                return response.data
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e("API Error", "Request failed: ${e.message}")
            return false
        }
    }
}