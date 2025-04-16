package com.yh.fridgesoksok.data.impl

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : FoodRepository {

    override fun getFoodList(): Flow<Resource<List<Food>>> = flow {
        emit(Resource.Loading())
        try {
            val foodList = remoteDataSource.getFoodList()
            val domainFoods = foodList.map { it.toDomain() }
            emit(Resource.Success(domainFoods))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }

    override fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>> = flow {
        emit(Resource.Loading())
        try {
            val tmp = remoteDataSource.uploadReceiptImage(img = img)
            val tmps = tmp.map { it.toDomain() }
            emit(Resource.Success(tmps))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.toString()))
        }
    }
}