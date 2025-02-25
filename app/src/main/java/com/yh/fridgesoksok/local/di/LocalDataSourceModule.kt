package com.yh.fridgesoksok.local.di

import com.yh.fridgesoksok.data.local.LocalUserDataSource
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.local.impl.LocalUserDataSourceImpl
import com.yh.fridgesoksok.remote.impl.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindLocalUserDataSource(source: LocalUserDataSourceImpl) : LocalUserDataSource
}