package com.yh.fridgesoksok.remote.di

import android.content.Context
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.MockApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {


    // Mock Api SERVER
    @Provides
    @Singleton
    fun provideFridgeApiService(
        @ApplicationContext context: Context
    ) : FridgeApiService = MockApiService(context)
}