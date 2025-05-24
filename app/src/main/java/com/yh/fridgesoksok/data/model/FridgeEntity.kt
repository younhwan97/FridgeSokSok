package com.yh.fridgesoksok.data.model

import com.yh.fridgesoksok.data.DataMapper
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.domain.model.Fridge

data class FridgeEntity(
    val id: String?,
    val fridgeName: String?,
    val createdAt: String?,
    val foodProducts: List<Food>?
) : DataMapper<Fridge> {
    override fun toDomain() =
        Fridge(id.orEmpty(), fridgeName.orEmpty(), createdAt.orEmpty(), foodProducts.orEmpty())
}

