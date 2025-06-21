package com.yh.fridgesoksok.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.di.FcmServiceEntryPoint
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FcmServiceEntryPoint::class.java
        )

        val updateUserFcmTokenUseCase = entryPoint.updateUserFcmTokenUseCase()
        val updateLocalUserFcmTokenUseCase = entryPoint.updateLocalUserFcmTokenUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            // FCM Local 저장
            updateLocalUserFcmTokenUseCase(token).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val isSuccess = result.data ?: false
                        if (isSuccess) {
                            Logger.d("FCM", "FCM Local 저장 성공")
                        }
                    }

                    is Resource.Loading -> Unit
                    is Resource.Error -> Unit
                }
            }
            // FCM Server 저장
            updateUserFcmTokenUseCase(token).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val isSuccess = result.data == token
                        if (isSuccess) {
                            Logger.d("FCM", "FCM Server 저장 성공")
                        }
                    }

                    is Resource.Loading -> Unit
                    is Resource.Error -> Unit
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "✅ 수신됨: ${message.data}")

        message.notification?.let {
            Log.d("FCM", "🔔 알림 내용: ${it.title} / ${it.body}")
        }

        // 필요 시 여기서 NotificationManager로 직접 알림 생성
    }
}