package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(fridgeId: String) =
        foodRepository.getFoods(fridgeId)
}