package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.MockApiService

// debug 빌드 전용: USE_MOCK_API=true 일 때 사용하는 Mock 서버
internal fun mockFridgeApiService(context: Context): FridgeApiService = MockApiService(context)
