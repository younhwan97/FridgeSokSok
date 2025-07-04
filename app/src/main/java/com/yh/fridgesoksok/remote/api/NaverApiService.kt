package com.yh.fridgesoksok.remote.api

import android.content.Context
import android.content.Intent
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.yh.fridgesoksok.service.NaverLoginActivity
import com.yh.fridgesoksok.remote.model.UserResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NaverApiService(
    private val appContext: Context
) {
    suspend fun createUserOnNaver(): UserResponse = suspendCancellableCoroutine { continuation ->
        if (NaverLoginActivity.pendingCallback != null) {
            continuation.resumeWithException(IllegalStateException("이미 로그인 요청이 진행 중입니다."))
            return@suspendCancellableCoroutine
        }

        NaverLoginActivity.pendingCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                try {
                    continuation.resume(
                        UserResponse(
                            id = "tmp",
                            accessToken = NaverIdLoginSDK.getAccessToken(),
                            refreshToken = NaverIdLoginSDK.getRefreshToken(),
                            username = "naver",
                            accountType = "NAVER",
                            defaultFridgeId = null
                        )
                    )
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val code = NaverIdLoginSDK.getLastErrorCode().code
                val desc = NaverIdLoginSDK.getLastErrorDescription()
                continuation.resumeWithException(
                    RuntimeException("Naver login failed: $httpStatus / $code: $desc")
                )
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        // 투명 Activity를 새 태스크로 띄워서 SDK.authenticate 를 위임
        val intent = Intent(appContext, NaverLoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)

        // 코루틴 취소 시 콜백 해제
        continuation.invokeOnCancellation {
            NaverLoginActivity.pendingCallback = null
        }
    }
}