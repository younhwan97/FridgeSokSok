package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.domain.model.SummaryFood
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : FoodRepository {

    override fun getSummaryFoods(): Flow<Resource<List<SummaryFood>>> = flow {
        emit(Resource.Loading())
        try {
            val foods = remoteDataSource.getSummaryFoods()
            val domainFoods = foods.map { it.toDomain() }
            emit(Resource.Success(domainFoods))
        } catch (exception: Exception) {
            // emit(Resource.Error(exception ?: "error"))
        }
    }
}