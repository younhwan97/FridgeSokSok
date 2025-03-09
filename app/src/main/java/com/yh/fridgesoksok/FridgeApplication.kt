package com.yh.fridgesoksok

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@HiltAndroidApp
class FridgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 카카오 API 키 셋팅
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}