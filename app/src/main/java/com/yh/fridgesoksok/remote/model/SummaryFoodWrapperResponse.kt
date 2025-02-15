package com.yh.fridgesoksok.remote.model

import com.google.gson.annotations.SerializedName

data class SummaryFoodWrapperResponse(
    @SerializedName("results")
    val summaryFoods: List<SummaryFoodResponse>
)
