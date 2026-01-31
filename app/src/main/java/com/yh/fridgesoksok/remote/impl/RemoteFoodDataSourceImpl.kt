package com.yh.fridgesoksok.remote.impl

import android.graphics.Bitmap
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
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    override suspend fun updateFood(foodEntity: FoodEntity): FoodEntity {
        val domainFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return try {
            Logger.d("RemoteFoodData", "updateFood INPUT $foodEntity")
            val response = fridgeApiService.updateFood(
                foodId = foodEntity.id,
                itemName = foodEntity.itemName,
                expiryDate = LocalDate.parse(foodEntity.expiryDate, domainFormatter).format(serverFormatter),
                categoryId = foodEntity.categoryId,
                count = foodEntity.count
            )
            val data = response.data ?: throw IllegalStateException("updateFood data(=null)")
            data.toEntity()
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "updateFood 실패", e)
            throw e
        }
    }

    override suspend fun deleteFood(foodId: String): Boolean {
        return try {
            Logger.d("RemoteFoodData", "deleteFood INPUT $foodId")
            val response = fridgeApiService.deleteFood(
                foodId = foodId
            )
            val data = response.status
            data == 200
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "deleteFood 실패", e)
            throw e
        }
    }

    override suspend fun uploadReceiptImage(img: Bitmap): List<ReceiptEntity> {
        val tempFile = File.createTempFile("receipt_", ".jpg")
        return try {
            Logger.d("RemoteFoodData", "uploadReceipt INPUT $img")
            val resized = resizeBitmap(img, 1080, 1080)
            val part = bitmapToMultipart(resized, tempFile)
            // 리사이즈된 Bitmap은 파일로 저장 후 더 이상 필요 없으므로 메모리 해제
            resized.recycle()
            val response = fridgeApiService.uploadReceiptImage(part)
            val data = response.data ?: throw IllegalStateException("uploadReceipt data(=null)")
            data.map { it.toEntity() }
        } catch (e: Exception) {
            Logger.e("RemoteFoodData", "uploadReceipt 실패", e)
            throw e
        } finally {
            // 업로드 성공/실패 여부와 관계없이 임시 파일 삭제
            tempFile.delete()
        }
    }
}

private fun bitmapToMultipart(bitmap: Bitmap, file: File, name: String = "image.jpg"): MultipartBody.Part {
    try {
        // use 블록으로 자동 close 보장
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", name, requestFile)
    } catch (e: Exception) {
        // 오류 발생 시 임시 파일 정리
        file.delete()
        throw e
    }
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

