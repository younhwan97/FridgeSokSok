package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class SearchLocalFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {

    operator fun invoke(keyword: String) =
        foodRepository.searchLocalFoods(keyword = keyword)
}