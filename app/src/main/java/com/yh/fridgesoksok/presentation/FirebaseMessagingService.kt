package com.yh.fridgesoksok.presentation

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "✅ 수신됨: ${message.data}")

        message.notification?.let {
            Log.d("FCM", "🔔 알림 내용: ${it.title} / ${it.body}")
        }

        // 필요 시 여기서 NotificationManager로 직접 알림 생성
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "🔄 새 토큰: $token")

        // 서버에 새 토큰 갱신 요청 필요 시 여기서 처리
    }
}