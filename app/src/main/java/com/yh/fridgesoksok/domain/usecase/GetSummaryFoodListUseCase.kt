package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository

class GetSummaryFoodListUseCase(
    private val foodRepository: FoodRepository
) {
    operator fun invoke() =
        foodRepository.getSummaryFoods()
}