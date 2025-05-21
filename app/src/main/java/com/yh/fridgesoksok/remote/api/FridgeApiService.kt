package com.yh.fridgesoksok.remote.api

import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FoodAddRequest
import com.yh.fridgesoksok.remote.model.FoodResponse
import com.yh.fridgesoksok.remote.model.FridgeResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.ReceiptResponse
import com.yh.fridgesoksok.remote.model.UserTmpCreateRequest
import com.yh.fridgesoksok.remote.model.UserCreateRequest
import com.yh.fridgesoksok.remote.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FridgeApiService {
    /* ********************************************************** */
    /* ************************ Food API ************************ */
    @Headers("Content-Type: application/json")
    @POST("foods")
    suspend fun addFoods(
        @Query("fridgeId") fridgeId: String,
        @Body foodList: List<FoodAddRequest>
    ): CommonResponse<List<FoodResponse>>

    @GET("foods/fridge/{fridgeId}")
    suspend fun getFoods(
        @Path("fridgeId") fridgeId: String
    ): CommonResponse<List<FoodResponse>>

    @PATCH("foods/{foodId}")
    suspend fun updateFood(
        @Path("foodId") foodId: String,
        @Query("itemName") itemName: String,
        @Query("expiryDate") expiryDate: String,
        @Query("categoryId") categoryId: Int,
        @Query("count") count: Int,
    ): CommonResponse<FoodResponse>

    @DELETE("foods/{foodId}")
    suspend fun deleteFood(
        @Path("foodId") foodId: String
    ): CommonResponse<Any>

    /* ************************************************************* */
    /* ************************ Receipt API ************************ */
    @Multipart
    @POST("receipts/upload")
    suspend fun uploadReceiptImage(
        @Part file: MultipartBody.Part
    ): CommonResponse<List<ReceiptResponse>>

    /* ************************************************************* */
    /* ************************ Receipt API ************************ */
    @GET("fridges")
    suspend fun getFridges(): CommonResponse<List<FridgeResponse>>

    @POST("fridges")
    suspend fun createFridge(
        @Query("fridgeName") fridgeName: String
    ): CommonResponse<FridgeResponse>

    @DELETE("fridges")
    suspend fun deleteAllFridges(): CommonResponse<Any>

    @DELETE("fridges/{fridgeId}")
    suspend fun deleteFridge(
        @Path("fridgeId") fridgeId: String
    ): CommonResponse<Any>

    @PATCH("fridges/{fridgeId}")
    suspend fun updateFridgeName(
        @Path("fridgeId") fridgeId: String,
        @Query("newName") newName: String
    ): CommonResponse<FridgeResponse>

    /* ************************************************************* */
    /* ************************ User API ************************ */
    @Headers("Content-Type: application/json")
    @POST("users/create")
    suspend fun createTmpUser(
        @Body userTmpCreateRequest: UserTmpCreateRequest
    ): CommonResponse<UserResponse>

    @GET("users/user/default-fridge")
    suspend fun getUserDefaultFridge(): CommonResponse<String>

    /* ************************************************************* */
    /* ************************ Oauth API ************************ */
    @Headers("Content-Type: application/json")
    @POST("auth/{provider}")
    suspend fun createUserOnServer(
        @Path("provider") provider: String,
        @Body userCreateRequest: UserCreateRequest
    ): CommonResponse<UserResponse>

    @POST("auth/refresh")
    suspend fun reissueUserToken(): CommonResponse<TokenResponse>

    @GET("auth/validateRefreshToken")
    suspend fun validateUserToken(): CommonResponse<Boolean>
}