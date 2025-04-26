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

    override fun getFoodList(): Flow<Resource<List<Food>>> =
        flowWithResource {
            remoteDataSource.getFoodList().map { it.toDomain() }
        }

    override fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>> =
        flowWithResource {
            remoteDataSource.uploadReceiptImage(img = img).map { it.toDomain() }
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