package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.data.model.FoodEntity
import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.data.remote.RemoteFoodDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.model.toEntity
import com.yh.fridgesoksok.remote.model.toRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "RemoteFoodData"

private val domainDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private val serverDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

class RemoteFoodDataSourceImpl @Inject constructor(
    private val fridgeApiService: FridgeApiService
) : RemoteFoodDataSource {

    override suspend fun addFoods(fridgeId: String, foods: List<FoodEntity>): List<FoodEntity> =
        apiCall(TAG, "addFoods", "$fridgeId, $foods") {
            fridgeApiService.addFoods(
                fridgeId = fridgeId,
                foodList = foods.map { it.toRequest() }
            ).requireData("addFoods").map { it.toEntity() }
        }

    override suspend fun getFoods(fridgeId: String): List<FoodEntity> =
        apiCall(TAG, "getFoods", fridgeId) {
            fridgeApiService.getFoods(fridgeId = fridgeId)
                .requireData("getFoods").map { it.toEntity() }
        }

    override suspend fun updateFood(foodEntity: FoodEntity): FoodEntity =
        apiCall(TAG, "updateFood", foodEntity) {
            fridgeApiService.updateFood(
                foodId = foodEntity.id,
                itemName = foodEntity.itemName,
                expiryDate = LocalDate.parse(foodEntity.expiryDate, domainDateFormatter).format(serverDateFormatter),
                categoryId = foodEntity.categoryId,
                count = foodEntity.count
            ).requireData("updateFood").toEntity()
        }

    override suspend fun deleteFood(foodId: String): Boolean =
        apiCall(TAG, "deleteFood", foodId) {
            fridgeApiService.deleteFood(foodId = foodId).status == 200
        }

    override suspend fun uploadReceiptImage(image: ByteArray): List<ReceiptEntity> =
        apiCall(TAG, "uploadReceipt", "${image.size} bytes") {
            val requestBody = image.toRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
            fridgeApiService.uploadReceiptImage(part)
                .requireData("uploadReceipt").map { it.toEntity() }
        }
}
