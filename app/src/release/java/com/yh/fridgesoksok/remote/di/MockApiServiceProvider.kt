package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.remote.api.FridgeApiService

// release 빌드에는 Mock 서버가 포함되지 않음 (USE_MOCK_API 는 release 에서 항상 false)
internal fun mockFridgeApiService(context: Context): FridgeApiService =
    error("Mock API 는 release 빌드에서 사용할 수 없습니다")
