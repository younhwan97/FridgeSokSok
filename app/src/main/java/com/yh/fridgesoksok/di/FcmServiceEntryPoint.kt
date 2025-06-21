package com.yh.fridgesoksok.di

import com.yh.fridgesoksok.domain.usecase.UpdateLocalUserFcmTokenUseCase
import com.yh.fridgesoksok.domain.usecase.UpdateUserFcmTokenUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FcmServiceEntryPoint {
    fun updateUserFcmTokenUseCase(): UpdateUserFcmTokenUseCase

    fun updateLocalUserFcmTokenUseCase(): UpdateLocalUserFcmTokenUseCase
}