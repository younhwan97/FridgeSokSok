package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class GetFoodListUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke() =
        foodRepository.getFoodList()
}