package com.yh.fridgesoksok.remote.api

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoApiService(
    private val context: Context
) {

    suspend fun createKakaoToken(): String = suspendCancellableCoroutine { continuation ->

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정 로그인 실패", error)
                continuation.resumeWithException(error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정 로그인 성공 ${token.accessToken}")
                continuation.resume(token.accessToken)
            }
        }

        // 카카오톡이 설치되어 있을 때
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            // 카카오톡 로그인
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡 로그인 성공 ${token.accessToken}")
                    continuation.resume(token.accessToken)
                }
            }
        } else {
            // 카카오계정 로그인
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }
}