package com.yh.fridgesoksok.domain.usecase

class GetSummaryFoodListUseCase(
    private val foodRepository: FoodRepository
) {
    operator fun invoke() =
        foodRepository.getSummaryFoods()
}