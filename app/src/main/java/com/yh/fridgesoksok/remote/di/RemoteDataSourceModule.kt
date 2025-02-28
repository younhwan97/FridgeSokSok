package com.yh.fridgesoksok.remote.di

import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.impl.RemoteDataSourceImpl
import com.yh.fridgesoksok.remote.impl.RemoteUserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(source: RemoteDataSourceImpl) : RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteUserDataSource(source: RemoteUserDataSourceImpl) : RemoteUserDataSource
}