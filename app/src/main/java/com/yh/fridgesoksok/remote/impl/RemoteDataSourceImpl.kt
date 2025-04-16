package com.yh.fridgesoksok.remote.impl

import android.graphics.Bitmap
import android.util.Log
import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.remote.RemoteDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.UserRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteDataSource {

    override suspend fun getFoodList(): List<FoodEntity> {
        return fridgeApiService.getFoodList().foodList.map { it.toData() }
    }

    override suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity> {
        try {
            // 입력값 로깅
            Log.d("INPUT(receipt): ", img.toString())
            // API 요청
            val resized = resizeBitmap(img, 1080, 1080)
            val part = bitmapToMultipart(resized)
            val response = fridgeApiService.uploadReceiptImage(part)
            val receipt = response.data
            // 출력값 로깅 및 리턴
            Log.d("OUTPUT(receipt): ", receipt.toString())
            return receipt.map { it.toData() }
        } catch (e: Exception) {
            val errorBody = (e as? HttpException)?.response()//?.errorBody()?.string()
            val errorMsg = e.message
            Log.e("ERROR(receipt)", "Error: $errorMsg, Body: $errorBody")
            return emptyList()
        }
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