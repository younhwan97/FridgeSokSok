package com.yh.fridgesoksok.remote.impl

import android.graphics.Bitmap
import android.util.Log
import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.toEntity
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

    override suspend fun addFoods(fridgeId: String, foods: List<FoodEntity>): List<FoodEntity> {
        return try {
            Logger.d("RemoteFoodData", "addFoods INPUT $fridgeId, $foods")
            val response = fridgeApiService.addFoods(
                fridgeId = fridgeId,
                foodList = foods.map { it.toRequest() }
            )
            val data = response.data ?: throw IllegalStateException("addFoods data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "addFoods 실패", e)
            throw e
        }
    }

    override suspend fun getFoods(fridgeId: String): List<FoodEntity> {
        return try {
            Logger.d("RemoteFoodData", "getFoods INPUT $fridgeId")
            val response = fridgeApiService.getFoods(
                fridgeId = fridgeId
            )
            val data = response.data ?: throw IllegalStateException("getFoods data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "getFoods 실패", e)
            throw e
        }
    }

    override suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity> {
        return try {
            Logger.d("RemoteFoodData", "uploadReceipt INPUT $img")
            val resized = resizeBitmap(img, 1080, 1080)
            val part = bitmapToMultipart(resized)
            val response = fridgeApiService.uploadReceiptImage(part)
            val data = response.data ?: throw IllegalStateException("uploadReceipt data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "uploadReceipt 실패", e)
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

