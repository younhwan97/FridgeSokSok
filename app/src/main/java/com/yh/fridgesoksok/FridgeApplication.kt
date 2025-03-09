package com.yh.fridgesoksok

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 카카오 API 키 셋팅
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}