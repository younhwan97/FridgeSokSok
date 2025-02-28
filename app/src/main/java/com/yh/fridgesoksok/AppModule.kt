package com.yh.fridgesoksok

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}