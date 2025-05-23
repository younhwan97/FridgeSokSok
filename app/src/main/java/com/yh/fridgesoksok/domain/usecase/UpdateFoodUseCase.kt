package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class UpdateFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(food: Food) =
        foodRepository.updateFood(food)
}