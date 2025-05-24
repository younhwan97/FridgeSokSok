package com.yh.fridgesoksok.remote.api

import android.content.Context
import android.content.Intent
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.yh.fridgesoksok.presentation.login.comp.NaverLoginActivity
import com.yh.fridgesoksok.remote.model.UserResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NaverApiService(
    private val appContext: Context
) {
    suspend fun createUserOnNaver(): UserResponse = suspendCancellableCoroutine { continuation ->
        NaverLoginActivity.pendingCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                try {
                    continuation.resume(
                        UserResponse(
                            id = 0,
                            accessToken = NaverIdLoginSDK.getAccessToken(),
                            refreshToken = NaverIdLoginSDK.getRefreshToken(),
                            username = "",
                            accountType = "NAVER"
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