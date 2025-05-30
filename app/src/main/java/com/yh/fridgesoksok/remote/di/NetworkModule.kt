package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.LoggingHeaderInterceptor
import com.yh.fridgesoksok.remote.api.MockApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
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

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        // Authorization 감추지 않도록
        redactHeader("Nothing")
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(requestHeaderInterceptor: RequestHeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
            addInterceptor(requestHeaderInterceptor)            // 1. 토큰 주입
            addInterceptor(LoggingHeaderInterceptor())          // 2. 토큰 직접 로그
            addInterceptor(loggingInterceptor)                  // 3. 전체 요청/응답 로그
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
    ): FridgeApiService = retrofit.create(FridgeApiService::class.java)

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


    // Naver Api SERVER
    @Provides
    @Singleton
    fun provideNaverApiService(
        @ApplicationContext context: Context
    ): NaverApiService = NaverApiService(context)
}