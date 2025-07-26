package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.common.ApiUsage
import com.yh.fridgesoksok.data.model.ApiUsageEntity

data class ApiUsageRespond(
    val recipeUsed: Int,
    val recipeLimit: Int,
    val recipeRemaining: Int,
    val receiptUsed: Int,
    val receiptLimit: Int,
    val receiptRemaining: Int,
    val premium: Boolean
)

fun ApiUsageRespond.toEntity() =
    listOf(
        ApiUsageEntity(
            feature = ApiUsage.RECIPE_GENERATION.key,
            max = recipeLimit,
            used = recipeUsed
        ),
        ApiUsageEntity(
            feature = ApiUsage.RECEIPT_OCR.key,
            max = receiptLimit,
            used = receiptUsed
        )
    )