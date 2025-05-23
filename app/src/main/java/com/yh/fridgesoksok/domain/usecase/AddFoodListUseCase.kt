package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class AddFoodListUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(foods: List<Food>) =
        foodRepository.addFoods(foods)
}