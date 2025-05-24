package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class AddFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(fridgeId: String, foods: List<Food>) =
        foodRepository.addFoods(fridgeId, foods)
}