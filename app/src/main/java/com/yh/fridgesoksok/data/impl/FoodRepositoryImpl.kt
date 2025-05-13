package com.yh.fridgesoksok.data.impl

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.data.model.toEntity
import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val remoteFoodDataSource: RemoteFoodDataSource
) : FoodRepository {

    override fun addFoodList(foods: List<Food>): Flow<Resource<List<Food>>> =
        flowWithResource {
            remoteFoodDataSource.addFoodList(foods.map { it.toEntity() }).map { it.toDomain() }
        }

    override fun getFoodList(): Flow<Resource<List<Food>>> =
        flowWithResource {
            remoteFoodDataSource.getFoodList().map { it.toDomain() }
        }

    override fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>> =
        flowWithResource {
            remoteFoodDataSource.uploadReceiptImage(img = img).map { it.toDomain() }
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