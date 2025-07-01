package com.yh.fridgesoksok.data.repository

import android.graphics.Bitmap
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFoodRepository : FoodRepository {

    private val foodMap = mutableMapOf<String, MutableList<Food>>()

    override fun addFoods(fridgeId: String, foods: List<Food>): Flow<Resource<List<Food>>> {
        val list = foodMap.getOrPut(fridgeId) { mutableListOf() }
        list.addAll(foods)
        return flow { emit(Resource.Success(foods)) }
    }

    override fun getFoods(fridgeId: String): Flow<Resource<List<Food>>> {
        val foods = foodMap[fridgeId].orEmpty()
        return flow { emit(Resource.Success(foods)) }
    }

    override fun updateFood(food: Food): Flow<Resource<Food>> {
        TODO("Not yet implemented")
    }

    override fun deleteFood(foodId: String): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun uploadReceiptImage(img: Bitmap): Flow<Resource<List<Receipt>>> {
        TODO("Not yet implemented")
    }

    override fun initializeLocalFoods(foods: List<LocalFood>): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun getCountLocalFoods(): Flow<Resource<Int>> {
        TODO("Not yet implemented")
    }

    override fun deleteLocalFoods(): Flow<Resource<Int>> {
        TODO("Not yet implemented")
    }

    override fun searchLocalFoods(keyword: String): Flow<Resource<List<LocalFood>>> {
        TODO("Not yet implemented")
    }
}