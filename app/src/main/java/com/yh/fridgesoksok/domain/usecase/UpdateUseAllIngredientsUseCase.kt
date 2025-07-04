package com.yh.fridgesoksok.domain.usecase

import com.yh.fridgesoksok.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUseAllIngredientsUseCase @Inject constructor(
    private val repository: UserRepository
) {

    operator fun invoke(enabled: Boolean) =
        repository.updateUseAllIngredients(enabled)
}