package com.yh.fridgesoksok.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.di.FcmServiceEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("FCM", "새로운 FCM 토큰 수신: ${token.take(20)}...")

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FcmServiceEntryPoint::class.java
        )

        val updateUserFcmTokenUseCase = entryPoint.updateUserFcmTokenUseCase()
        val updateLocalUserFcmTokenUseCase = entryPoint.updateLocalUserFcmTokenUseCase()
        val loadUserUseCase = entryPoint.loadUserUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            // 1. FCM 토큰 로컬 저장 (항상 수행)
            updateLocalUserFcmTokenUseCase(token).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val isSuccess = result.data ?: false
                        if (isSuccess) {
                            Logger.d("FCM", "FCM 토큰 로컬 저장 성공")
                        } else {
                            Logger.e("FCM", "FCM 토큰 로컬 저장 실패")
                        }
                    }

                    is Resource.Error -> {
                        Logger.e("FCM", "FCM 토큰 로컬 저장 에러: ${result.message}")
                    }

                    is Resource.Loading -> Unit
                }
            }

            // 2. FCM 토큰 서버 저장 (로그인된 경우만 수행)
            val user = loadUserUseCase()
            if (user.accessToken.isNotBlank() && user.refreshToken.isNotBlank()) {
                Logger.d("FCM", "사용자 로그인 확인됨, 서버에 FCM 토큰 동기화 시도")
                updateUserFcmTokenUseCase(token).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val isSuccess = result.data == token
                            if (isSuccess) {
                                Logger.d("FCM", "FCM 토큰 서버 저장 성공")
                            } else {
                                Logger.d("FCM", "FCM 토큰 서버 저장 응답 불일치")
                            }
                        }

                        is Resource.Error -> {
                            Logger.e("FCM", "FCM 토큰 서버 저장 에러: ${result.message}")
                        }

                        is Resource.Loading -> Unit
                    }
                }
            } else {
                Logger.d("FCM", "사용자 미로그인 상태, 서버 동기화 스킵 (로그인 시 자동 동기화됨)")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}