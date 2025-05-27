package com.yh.fridgesoksok.data.di

import com.yh.fridgesoksok.data.impl.FoodRepositoryImpl
import com.yh.fridgesoksok.data.impl.RecipeRepositoryImpl
import com.yh.fridgesoksok.data.impl.UserRepositoryImpl
import com.yh.fridgesoksok.domain.repository.FoodRepository
import com.yh.fridgesoksok.domain.repository.RecipeRepository
import com.yh.fridgesoksok.domain.repository.UserRepository
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

    @Binds
    @Singleton
    abstract fun bindUserRepository(repo: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(repo: RecipeRepositoryImpl): RecipeRepository
}