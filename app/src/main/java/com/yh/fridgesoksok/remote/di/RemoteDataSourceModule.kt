package com.yh.fridgesoksok.remote.di

import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.impl.RemoteFoodDataSourceImpl
import com.yh.fridgesoksok.remote.impl.RemoteRecipeDataSourceImpl
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
    abstract fun bindRemoteDataSource(source: RemoteFoodDataSourceImpl) : RemoteFoodDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteUserDataSource(source: RemoteUserDataSourceImpl) : RemoteUserDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteRecipeDataSource(source: RemoteRecipeDataSourceImpl) : RemoteRecipeDataSource
}