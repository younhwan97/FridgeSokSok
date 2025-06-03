package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.model.LocalFood
import com.yh.fridgesoksok.domain.repository.FoodRepository
import javax.inject.Inject

class InitializeLocalFoodsFromCsvUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    operator fun invoke(foods: List<LocalFood>) =
        repository.initializeLocalFoods(foods = foods)
}