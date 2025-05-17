package com.yh.fridgesoksok.remote.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestHeaderInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val prefs = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("accessToken", null)
        val refreshToken = prefs.getString("refreshToken", null)

        val url = originalRequest.url.toString()

        val token = when {
            url.contains("auth/") -> refreshToken
            else -> accessToken
        }

        val requestWithHeaders = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestWithHeaders)
    }
}