package com.yh.fridgesoksok.remote.impl

import android.util.Log
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.model.TokenResponse
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
        try {
            // 입력값 로깅
            Log.d("INPUT(provider): ", user.toString())
            // API 요청
            val userRequest = UserRequest(token = user.accessToken, username = user.id.toString())
            val response = fridgeApiService.createUser(userRequest = userRequest)
            val userResponse: UserResponse = response.data
            // 출력값 로깅 및 리턴
            Log.d("OUTPUT(provider): ", userResponse.toString())
            return userResponse.toData()
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message
            Log.e("ERROR(provider)", "Error: $errorMsg, Body: $errorBody")
            return UserEntity(id = -1L, null, null, null, null)
        }
    }

    override suspend fun validateUserToken(refreshToken: String): Boolean {
        try {
            // 입력값 로깅
            Log.d("INPUT(validateRefreshToken): ", refreshToken)
            // API 요청
            val response = fridgeApiService.validateUserToken()
            val isValidate: Boolean = response.data
            // 출력값 로깅 및 리턴
            Log.d("OUTPUT(validateRefreshToken): ", isValidate.toString())
            return isValidate
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message
            Log.e("ERROR(validateRefreshToken)", "Error: $errorMsg, Body: $errorBody")
            return false
        }
    }

    override suspend fun reissueUserToken(refreshToken: String): TokenEntity {
        try {
            // 입력값 로깅
            Log.d("INPUT(refresh): ", refreshToken)
            // API 요청
            val response = fridgeApiService.reissueUserToken()
            val tokenResponse: TokenResponse = response.data
            // 출력값 로깅 및 리턴
            Log.d("OUTPUT(refresh): ", tokenResponse.toString())
            return tokenResponse.toData()
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
            val errorMsg = e.message
            Log.e("ERROR(refresh)", "Error: $errorMsg, Body: $errorBody")
            return TokenEntity("-1", "-1")
        }
    }
}