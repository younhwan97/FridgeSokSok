package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserWrapperResponse
import java.io.InputStream

class MockApiService (
    private val context: Context
) : FridgeApiService {

    override suspend fun getSummaryFoods(): SummaryFoodWrapperResponse {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

        context.assets.open("foods.json").use { inputStream: InputStream ->
            val size: Int = inputStream.available()
            val buffer = ByteArray(size).apply {
                inputStream.read(this)
            }
            return gson.fromJson(String(buffer), SummaryFoodWrapperResponse::class.java)
        }
    }

    override suspend fun createUser(userRequest: UserRequest): UserWrapperResponse{
        TODO("Not yet implemented")
    }

    override suspend fun validateUserToken(): CommonResponse {
        return CommonResponse(
            data = true,
            message = "",
            status = 200
        )
    }
}