package com.yh.fridgesoksok.local.di

import android.content.Context
import androidx.room.Room
import com.yh.fridgesoksok.local.room.AppDatabase
import com.yh.fridgesoksok.local.room.dao.FoodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalRoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "FOOD"
        )
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideFoodDao(database: AppDatabase): FoodDao = database.foodDao()
}