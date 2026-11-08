package com.yh.fridgesoksok.data.impl

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.common.flowWithResource
import com.yh.fridgesoksok.data.local.LocalFoodDataSource
import com.yh.fridgesoksok.data.model.toDomain
import com.yh.fridgesoksok.data.model.toEntity
import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val remoteFoodDataSource: RemoteFoodDataSource,
    private val localFoodDataSource: LocalFoodDataSource
) : FoodRepository {

    override fun addFoods(fridgeId: String, foods: List<Food>): Flow<Resource<List<Food>>> =
        flowWithResource {
            remoteFoodDataSource.addFoods(fridgeId, foods.map { it.toEntity() }).map { it.toDomain() }
        }

    override fun getFoods(fridgeId: String): Flow<Resource<List<Food>>> =
        flowWithResource {
            remoteFoodDataSource.getFoods(fridgeId).map { it.toDomain() }
        }

    override fun updateFood(food: Food): Flow<Resource<Food>> =
        flowWithResource {
            remoteFoodDataSource.updateFood(food.toEntity()).toDomain()
        }

    override fun deleteFood(foodId: String): Flow<Resource<Boolean>> =
        flowWithResource {
            remoteFoodDataSource.deleteFood(foodId)
        }

    override fun uploadReceiptImage(image: ByteArray): Flow<Resource<List<Receipt>>> =
        flowWithResource {
            remoteFoodDataSource.uploadReceiptImage(image = image).map { it.toDomain() }
        }

    override fun initializeLocalFoods(foods: List<LocalFood>): Flow<Resource<Boolean>> =
        flowWithResource {
            localFoodDataSource.insertFoods(foods.map { it.toEntity() })
        }

    override fun getCountLocalFoods(): Flow<Resource<Int>> =
        flowWithResource {
            localFoodDataSource.getCount()
        }

    override fun deleteLocalFoods(): Flow<Resource<Int>> =
        flowWithResource {
            localFoodDataSource.deleteFoods()
        }

    override fun searchLocalFoods(keyword: String): Flow<Resource<List<LocalFood>>> =
        flowWithResource {
            localFoodDataSource.searchFoods(keyword).map { it.toDomain() }
        }
}