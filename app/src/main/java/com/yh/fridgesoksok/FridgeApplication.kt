package com.yh.fridgesoksok

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FridgeApplication : Application() {
    val baseUrl: String
        get() = "http://soksok.io/api/"

    override fun onCreate() {
        super.onCreate()

        // 카카오 API 키 셋팅
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)

        // 네이버 API 키 셋팅
        NaverIdLoginSDK.initialize(
            context = this,
            clientId = BuildConfig.NAVER_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
            clientName = BuildConfig.NAVER_CLIENT_APP_NAME
        )
        NaverIdLoginSDK.showDevelopersLog(true)

        // 파이어베이스 초기화
        FirebaseApp.initializeApp(this)

        // 알림 채널 생성
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(NotificationChannel(
            "default",
            "기본 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "푸시 알림을 위한 기본 채널입니다."
        })
    }
}