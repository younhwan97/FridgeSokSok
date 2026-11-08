package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.BuildConfig
import com.yh.fridgesoksok.remote.api.AuthApiService
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.MockApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
import com.yh.fridgesoksok.remote.api.RequestHeaderInterceptor
import com.yh.fridgesoksok.remote.api.TokenAuthenticator
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

    private const val TIME_OUT = 30L

    // 릴리스 빌드에서는 로그를 남기지 않고, 디버그에서도 토큰은 마스킹
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    private fun baseClientBuilder(): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)

    private fun createRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        requestHeaderInterceptor: RequestHeaderInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        baseClientBuilder()
            .addInterceptor(requestHeaderInterceptor)   // 1. 토큰 주입
            .addInterceptor(loggingInterceptor)         // 2. 요청/응답 로그 (디버그 전용)
            .authenticator(tokenAuthenticator)          // 3. 401 시 토큰 재발급 후 재시도
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        createRetrofit(okHttpClient)

    // 토큰 재발급 전용 (Authenticator 순환 의존 방지를 위해 별도 클라이언트 사용)
    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService =
        createRetrofit(baseClientBuilder().addInterceptor(loggingInterceptor).build())
            .create(AuthApiService::class.java)

    // Api SERVER (local.properties 의 USE_MOCK_API=true 이면 Mock 서버 사용)
    @Provides
    @Singleton
    fun provideFridgeApiService(
        retrofit: Retrofit,
        @ApplicationContext context: Context
    ): FridgeApiService =
        if (BuildConfig.USE_MOCK_API) MockApiService(context)
        else retrofit.create(FridgeApiService::class.java)

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
