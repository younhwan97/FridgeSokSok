package com.yh.fridgesoksok.data.di

import com.yh.fridgesoksok.data.impl.FoodRepositoryImpl
import com.yh.fridgesoksok.domain.repository.FoodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFoodRepository(repo: FoodRepositoryImpl): FoodRepository
}