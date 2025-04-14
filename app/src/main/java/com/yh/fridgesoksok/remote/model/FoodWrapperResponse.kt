package com.yh.fridgesoksok.remote.model

import com.google.gson.annotations.SerializedName

data class FoodWrapperResponse(
    @SerializedName("results")
    val foodList: List<FoodResponse>
)
