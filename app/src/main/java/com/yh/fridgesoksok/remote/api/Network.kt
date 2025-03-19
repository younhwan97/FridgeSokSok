package com.yh.fridgesoksok.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIME_OUT = 30

fun createApiService(baseUrl: String): FridgeApiService {
    val okHttpClient = OkHttpClient.Builder().apply {
        readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        //addNetworkInterceptor(RequestHeaderInterceptor())

    }.build()

    return Retrofit.Builder()
        .baseUrl("https://soksok.io/api/")
        //.client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FridgeApiService::class.java)

}