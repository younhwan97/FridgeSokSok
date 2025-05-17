package com.yh.fridgesoksok.remote.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authHeader = request.header("Authorization")

        Log.d("AuthHeader", "➡️ [${request.method}] ${request.url}")
        Log.d("AuthHeader", "🔑 Authorization: $authHeader")

        return chain.proceed(request)
    }
}