package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.MockApiService
import com.yh.fridgesoksok.remote.api.RequestHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val TIME_OUT = 30

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // ★ 모든 바디 출력
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(requestHeaderInterceptor: RequestHeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            addNetworkInterceptor(requestHeaderInterceptor)
            addInterceptor(loggingInterceptor)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://soksok.io/api/") // API 기본 URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Api SERVER
    @Provides
    @Singleton
    fun provideFridgeApiService(
        retrofit: Retrofit,
        @ApplicationContext context: Context
    ): FridgeApiService =  retrofit.create(FridgeApiService::class.java)

    // Mock Api SERVER
//    @Provides
//    @Singleton
//    fun provideFridgeApiService(
//        retrofit: Retrofit,
//        @ApplicationContext context: Context
//    ): FridgeApiService = MockApiService(context)

    // Kakao Api SERVER
    @Provides
    @Singleton
    fun provideKakaoApiService(
        @ApplicationContext context: Context
    ): KakaoApiService = KakaoApiService(context)
}