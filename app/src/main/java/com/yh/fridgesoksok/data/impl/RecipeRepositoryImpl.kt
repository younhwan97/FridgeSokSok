package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.model.toDomain
import com.yh.fridgesoksok.data.model.toEntity
import com.yh.fridgesoksok.data.remote.RemoteRecipeDataSource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Recipe
import com.yh.fridgesoksok.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val remoteRecipeDataSource: RemoteRecipeDataSource
) : RecipeRepository {

    override fun createRecipe(foods: Collection<Food>): Flow<Resource<Recipe>> =
        flowWithResource {
            remoteRecipeDataSource.createRecipe(foods.map { it.toEntity() }).toDomain()
        }

    override fun getRecipes(): Flow<Resource<List<Recipe>>> =
        flowWithResource {
            remoteRecipeDataSource.getRecipes().map { it.toDomain() }
        }

    private inline fun <T> flowWithResource(crossinline block: suspend () -> T): Flow<Resource<T>> =
        flow {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(block()))
            } catch (exception: Exception) {
                emit(Resource.Error(exception.localizedMessage ?: "Unknown Error"))
            }
        }
}