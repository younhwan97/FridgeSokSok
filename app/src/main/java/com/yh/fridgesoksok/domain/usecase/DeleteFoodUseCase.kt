package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class DeleteFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(foodId: String) =
        foodRepository.deleteFood(foodId)
}