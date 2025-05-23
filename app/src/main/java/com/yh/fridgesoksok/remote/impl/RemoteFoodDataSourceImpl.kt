package com.yh.fridgesoksok.remote.impl

import android.graphics.Bitmap
import android.util.Log
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.toRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RemoteFoodDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteFoodDataSource {

    companion object {
        private const val TAG = "RemoteDataLogger"
    }

    private fun logInput(action: String, input: Any?) {
        Log.d(TAG, "[$action][INPUT] $input")
    }

    private fun logOutput(action: String, output: Any?) {
        Log.d(TAG, "[$action][OUTPUT] $output")
    }

    private fun logError(action: String, e: Exception) {
        val httpException = e as? HttpException
        val errorBody = httpException?.response()?.errorBody()?.string()
        val errorMsg = e.localizedMessage ?: "Unknown error"
        Log.e(TAG, "[$action][ERROR] Exception: $errorMsg\nBody: ${errorBody ?: "No error body"}")
    }

    override suspend fun addFoods(foods: List<FoodEntity>): List<FoodEntity> {
        val action = "addFoods"
        return try {
            logInput(action, foods)
            val response = fridgeApiService.addFoods(fridgeId = "", foodList = foods.map { it.toRequest() }).data
            logOutput(action, response)
            response.map { it.toData() }
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun getFoods(): List<FoodEntity> {
        return fridgeApiService.getFoods("temp").data.map { it.toData() }
    }

    override suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity> {
        val action = "uploadReceiptImage"
        return try {
            logInput(action, img)
            val resized = resizeBitmap(img, 1080, 1080)
            val part = bitmapToMultipart(resized)
            val response = fridgeApiService.uploadReceiptImage(part)
            val receipt = response.data
            logOutput(action, receipt)
            receipt.map { it.toData() }
        } catch (e: Exception) {
            logError(action, e)
            throw e
        }
    }

    override suspend fun updateFood(foodEntity: FoodEntity): FoodEntity {
        TODO("Not yet implemented")
    }
}

private fun bitmapToMultipart(bitmap: Bitmap, name: String = "image.jpg"): MultipartBody.Part {
    val file = File.createTempFile("temp", ".jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", name, requestFile)
}

private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val ratio = minOf(
        maxWidth.toFloat() / bitmap.width,
        maxHeight.toFloat() / bitmap.height
    )

    val width = (bitmap.width * ratio).toInt()
    val height = (bitmap.height * ratio).toInt()

    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

