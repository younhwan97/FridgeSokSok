package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.yh.fridgesoksok.remote.model.SummaryFoodWrapperResponse
import java.io.InputStream
import javax.inject.Inject

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
}