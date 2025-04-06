package com.yh.fridgesoksok.remote.impl

import android.util.Log
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import retrofit2.HttpException
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService,
    private val fridgeApiService: FridgeApiService
) : RemoteUserDataSource {

    override suspend fun createUserToken(channel: Channel): UserEntity =
        when (channel) {
            Channel.KAKAO -> kakaoApiService.createKakaoToken()
            Channel.NAVER -> TODO()
            Channel.GOOGLE -> TODO()
            Channel.FACEBOOK -> TODO()
            else -> UserResponse(id = -1L, null, null, null, null)
        }.toData()

    override suspend fun createUser(user: User): UserEntity {
        val userRequest = UserRequest(token = user.accessToken, username = user.id.toString())

        Log.d("INPUT(provider): ", userRequest.toString())

        try {
            val response = fridgeApiService.createUser(userRequest = userRequest)

            Log.d("OUTPUT(provider): ", response.toString())

            return UserResponse(
                id = user.id,
                accessToken = response.userResponse.accessToken,
                refreshToken = response.userResponse.refreshToken,
                username = response.userResponse.username,
                accountType = response.userResponse.accountType
            ).toData()
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message

            Log.e("ERROR(provider)", "Error: $errorMsg, Body: $errorBody")

            return UserEntity(id = -1L, null, null, null, null)
        }
    }

    override suspend fun validateUserToken(refreshToken: String): Boolean {
        Log.d("INPUT(validateRefreshToken): ", refreshToken)

        try {
            val response = fridgeApiService.validateUserToken()

            Log.d("OUTPUT(validateRefreshToken): ", response.toString())

            return response.data
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message

            Log.e("ERROR(validateRefreshToken)","Error: $errorMsg, Body: $errorBody")

            return false
        }
    }

    override suspend fun reissueUserToken(refreshToken: String): TokenEntity {
        Log.d("INPUT(refresh): ", refreshToken)

        try {
            val response = fridgeApiService.reissueUserToken()

            Log.d("OUTPUT(refresh): ", response.data.toString())

            return TokenEntity(accessToken = response.data.accessToken, refreshToken = response.data.refreshToken)
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message

            Log.e("ERROR(refresh)", "Error: $errorMsg, Body: $errorBody")

            return TokenEntity("-1", "-1")
        }
    }
}