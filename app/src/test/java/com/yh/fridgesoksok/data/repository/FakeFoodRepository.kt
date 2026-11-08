package com.yh.fridgesoksok.data.repository

import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.model.Receipt
import com.yh.fridgesoksok.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFoodRepository : FoodRepository {

    private val foodMap = mutableMapOf<String, MutableList<Food>>()
    private val localFoods = mutableListOf<LocalFood>()
    private val receipts = mutableListOf<Receipt>()

    // 마지막으로 업로드된 영수증 이미지 (테스트 검증용)
    var lastUploadedImage: ByteArray? = null
        private set

    fun setReceipts(items: List<Receipt>) {
        receipts.clear()
        receipts.addAll(items)
    }

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
        for ((_, foods) in foodMap) {
            val index = foods.indexOfFirst { it.id == food.id }
            if (index != -1) {
                foods[index] = food
                return flow { emit(Resource.Success(food)) }
            }
        }
        return flow { emit(Resource.Error("Food not found", null, 404)) }
    }

    override fun deleteFood(foodId: String): Flow<Resource<Boolean>> {
        var deleted = false
        for ((_, foods) in foodMap) {
            val removed = foods.removeIf { it.id == foodId }
            if (removed) {
                deleted = true
                break
            }
        }
        return flow { emit(Resource.Success(deleted)) }
    }

    override fun uploadReceiptImage(image: ByteArray): Flow<Resource<List<Receipt>>> {
        lastUploadedImage = image
        return flow { emit(Resource.Success(receipts.toList())) }
    }

    override fun initializeLocalFoods(foods: List<LocalFood>): Flow<Resource<Boolean>> {
        localFoods.clear()
        localFoods.addAll(foods)
        return flow { emit(Resource.Success(true)) }
    }

    override fun getCountLocalFoods(): Flow<Resource<Int>> {
        return flow { emit(Resource.Success(localFoods.size)) }
    }

    override fun deleteLocalFoods(): Flow<Resource<Int>> {
        val count = localFoods.size
        localFoods.clear()
        return flow { emit(Resource.Success(count)) }
    }

    override fun searchLocalFoods(keyword: String): Flow<Resource<List<LocalFood>>> {
        val results = localFoods.filter { it.itemName.contains(keyword, ignoreCase = true) }
        return flow { emit(Resource.Success(results)) }
    }
}