package com.yh.fridgesoksok.remote.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestHeaderInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_info", Context.MODE_PRIVATE)

        val refreshToken = sharedPreferences.getString("refreshToken", null)

        // refreshToken이 null이 아니면 Authorization 헤더에 토큰 추가
        val requestWithHeaders = if (refreshToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $refreshToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestWithHeaders)
    }
}