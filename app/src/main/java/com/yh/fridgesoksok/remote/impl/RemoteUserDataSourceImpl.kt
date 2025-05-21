package com.yh.fridgesoksok.remote.impl

import android.util.Log
import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
import com.yh.fridgesoksok.remote.model.TokenRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import retrofit2.HttpException
import javax.inject.Inject

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService,
    private val naverApiService: NaverApiService,
    private val fridgeApiService: FridgeApiService
) : RemoteUserDataSource {

    companion object {
        private const val TAG = "RemoteUserLogger"
    }

    private fun logInput(action: String, input: Any?) {
        Log.d(TAG, "[$action][INPUT] $input")
    }

    private fun logOutput(action: String, output: Any?) {
        Log.d(TAG, "[$action][OUTPUT] $output")
    }

    private fun logError(action: String, e: Exception) {
        val httpException = e as? HttpException
        val errorBody = httpException?.response()?.errorBody()?.string()
        val errorMsg = e.localizedMessage ?: "Unknown error"
        Log.e(TAG, "[$action][ERROR] Exception: $errorMsg\nBody: ${errorBody ?: "No error body"}")
    }

    override suspend fun createUserOnChannel(channel: Channel): UserEntity {
        val action = "createUserToken"
        return try {
            logInput(action, channel)
            val userToken = when (channel) {
                Channel.KAKAO -> kakaoApiService.createUserOnKakao()
                Channel.NAVER -> naverApiService.createUserOnNaver()
                else -> UserResponse(id = -1L, null, null, null, null)
            }
            logOutput(action, userToken)
            userToken.toData()
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun createUserOnServer(user: User): UserEntity {
        val action = "createUser"
        return try {
            logInput(action, user)
            val tokenRequest = TokenRequest(token = user.accessToken ?: "", username = user.id.toString())
            val response = when (user.accountType) {
                Channel.KAKAO.toString() -> fridgeApiService.createUserOnServer(provider = "kakao", tokenRequest = tokenRequest)
                Channel.NAVER.toString() -> fridgeApiService.createUserOnServer(provider = "naver", tokenRequest = tokenRequest)
                else -> fridgeApiService.createUserOnServer(provider = "", tokenRequest = tokenRequest)
            }
            val userResponse = response.data
            logOutput(action, userResponse)
            userResponse.toData()
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun validateUserToken(refreshToken: String): Boolean {
        val action = "validateUserToken"
        return try {
            logInput(action, refreshToken)
            val response = fridgeApiService.validateUserToken()
            val isValid = response.data
            logOutput(action, isValid)
            isValid
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun reissueUserToken(refreshToken: String): TokenEntity {
        val action = "reissueUserToken"
        return try {
            logInput(action, refreshToken)
            val response = fridgeApiService.reissueUserToken()
            val tokenResponse = response.data
            logOutput(action, tokenResponse)
            tokenResponse.toData()
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun getUserDefaultFridge(): String {
        val action = "defaultFridge"
        return try {
            logInput(action, action)
            val response = fridgeApiService.getUserDefaultFridge()
            val defaultFridge = response.data
            logOutput(action, defaultFridge)
            defaultFridge
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }
}