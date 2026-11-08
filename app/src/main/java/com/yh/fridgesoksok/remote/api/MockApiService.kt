package com.yh.fridgesoksok.remote.api

import android.content.Context
import com.yh.fridgesoksok.remote.model.ApiUsageResponse
import com.yh.fridgesoksok.remote.model.CommonResponse
import com.yh.fridgesoksok.remote.model.FcmRequest
import com.yh.fridgesoksok.remote.model.FoodAddRequest
import com.yh.fridgesoksok.remote.model.FoodResponse
import com.yh.fridgesoksok.remote.model.FridgeResponse
import com.yh.fridgesoksok.remote.model.ReceiptResponse
import com.yh.fridgesoksok.remote.model.RecipeRequest
import com.yh.fridgesoksok.remote.model.RecipeResponse
import com.yh.fridgesoksok.remote.model.ServerStatusResponse
import com.yh.fridgesoksok.remote.model.TokenResponse
import com.yh.fridgesoksok.remote.model.UserCreateRequest
import com.yh.fridgesoksok.remote.model.UserProfileRequest
import com.yh.fridgesoksok.remote.model.UserProfileResponse
import com.yh.fridgesoksok.remote.model.UserResponse
import com.yh.fridgesoksok.remote.model.UserSettingRequest
import com.yh.fridgesoksok.remote.model.UserSettingResponse
import com.yh.fridgesoksok.remote.model.UserTmpCreateRequest
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val MOCK_FRIDGE_ID = "mock-fridge"
private const val MOCK_TOKEN = "mock-token"
private const val RECIPE_LIMIT = 5
private const val RECEIPT_LIMIT = 5
private const val NETWORK_DELAY_MS = 300L

/**
 * 서버 없이 앱을 실행하기 위한 메모리 기반 Mock 서버 (local.properties 의 USE_MOCK_API=true 일 때 사용)
 * - 앱 프로세스가 살아 있는 동안 식품/레시피/설정/사용량 상태를 유지
 * - 카카오/네이버 로그인은 실제 SDK 를 거치므로, 서버 없이 테스트할 때는 게스트 로그인 사용
 */
class MockApiService(
    @Suppress("unused") private val context: Context
) : FridgeApiService {

    private val serverDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun today(): LocalDate = LocalDate.now()

    private val foods = mutableListOf(
        mockFood("서울우유", today().plusDays(5), categoryId = 6),
        mockFood("대패삼겹살", today().plusDays(2), categoryId = 11),
        mockFood("국산콩 두부", today().plusDays(1), categoryId = 17),
        mockFood("배추김치", today().plusDays(20), categoryId = 10),
        mockFood("비비고 왕교자", today().plusMonths(6), categoryId = 7),
    )
    private val recipes = mutableListOf<RecipeResponse>()
    private var settings = UserSettingResponse(isNotification = true, useAllIngredients = false, autoDeleteFoods = false)
    private var recipeUsed = 0
    private var receiptUsed = 0

    private fun mockFood(name: String, expiry: LocalDate, categoryId: Int, count: Int = 1) = FoodResponse(
        id = UUID.randomUUID().toString(),
        fridgeId = MOCK_FRIDGE_ID,
        itemName = name,
        expiryDate = expiry.format(serverDate),
        categoryId = categoryId,
        count = count,
        createdAt = today().format(serverDate)
    )

    private fun <T> ok(data: T, message: String = "Success") =
        CommonResponse(message = message, data = data, status = 200)

    // 실제 서버처럼 HTTP 오류를 던져 Resource.Error(status) 로 전달되도록 함
    private fun httpError(code: Int, message: String): Nothing =
        throw HttpException(Response.error<Any>(code, """{"message":"$message","data":null,"status":$code}""".toResponseBody()))

    /* ********************************************************** */
    /* ************************ Food API ************************ */
    override suspend fun addFoods(fridgeId: String, foodList: List<FoodAddRequest>): CommonResponse<List<FoodResponse>> {
        delay(NETWORK_DELAY_MS)
        val added = foodList.map {
            FoodResponse(
                id = UUID.randomUUID().toString(),
                fridgeId = fridgeId,
                itemName = it.itemName,
                expiryDate = it.expiryDate,
                categoryId = it.categoryId,
                count = it.count,
                createdAt = today().format(serverDate)
            )
        }
        synchronized(foods) { foods += added }
        return ok(added, "Food items added")
    }

    override suspend fun getFoods(fridgeId: String): CommonResponse<List<FoodResponse>> {
        delay(NETWORK_DELAY_MS)
        return ok(synchronized(foods) { foods.toList() }, "Food list retrieved")
    }

    override suspend fun updateFood(foodId: String, itemName: String, expiryDate: String, categoryId: Int, count: Int): CommonResponse<FoodResponse> {
        delay(NETWORK_DELAY_MS)
        val updated = synchronized(foods) {
            val index = foods.indexOfFirst { it.id == foodId }
            if (index < 0) httpError(404, "Food not found")
            foods[index].copy(itemName = itemName, expiryDate = expiryDate, categoryId = categoryId, count = count)
                .also { foods[index] = it }
        }
        return ok(updated)
    }

    override suspend fun deleteFood(foodId: String): CommonResponse<Any> {
        delay(NETWORK_DELAY_MS)
        synchronized(foods) { foods.removeAll { it.id == foodId } }
        return ok(true)
    }

    /* ************************************************************* */
    /* ************************ Receipt API ************************ */
    override suspend fun uploadReceiptImage(image: MultipartBody.Part): CommonResponse<List<ReceiptResponse>> {
        delay(1500)
        if (receiptUsed >= RECEIPT_LIMIT) httpError(429, "Receipt quota exceeded")
        receiptUsed++
        // 매칭(신선/가공) + 미매칭 항목이 섞인 영수증 예시
        return ok(
            listOf(
                ReceiptResponse(-168, "삼겹살", "냉장 3~5일", "육류", 72, 11, 1, "SAFETY"),
                ReceiptResponse(983327, "서울우유", "제조일로부터 140일", "우유", 3360, 6, 2, "LABEL"),
                ReceiptResponse(-120, "양파", "실온 1~2개월", "채소", 720, 1, 3, "QUALITY"),
                ReceiptResponse(null, "삼다수", null, null, null, null, 2, null),
            )
        )
    }

    /* ************************************************************* */
    /* ************************ Fridge API ************************* */
    private fun mockFridge() = FridgeResponse(
        id = MOCK_FRIDGE_ID,
        fridgeName = "우리집 냉장고",
        createdAt = today().format(serverDate),
        foodProducts = emptyList()
    )

    override suspend fun getFridges(): CommonResponse<List<FridgeResponse>> = ok(listOf(mockFridge()))

    override suspend fun createFridge(fridgeName: String): CommonResponse<FridgeResponse> =
        ok(mockFridge().copy(fridgeName = fridgeName))

    override suspend fun deleteAllFridges(): CommonResponse<Any> = ok(true)

    override suspend fun deleteFridge(fridgeId: String): CommonResponse<Any> = ok(true)

    override suspend fun updateFridgeName(fridgeId: String, newName: String): CommonResponse<FridgeResponse> =
        ok(mockFridge().copy(fridgeName = newName))

    /* ************************************************************* */
    /* ************************ User API *************************** */
    override suspend fun getServerStatus(): CommonResponse<ServerStatusResponse> {
        val now = LocalDateTime.now().toString()
        return ok(ServerStatusResponse(serverStartTimeKST = now, version = "mock", status = "UP", timestamp = now))
    }

    private fun mockUser(accountType: String) = UserResponse(
        id = "mock-user",
        accessToken = MOCK_TOKEN,
        refreshToken = MOCK_TOKEN,
        username = "목업 사용자",
        accountType = accountType,
        defaultFridgeId = MOCK_FRIDGE_ID
    )

    override suspend fun createTmpUser(userTmpCreateRequest: UserTmpCreateRequest): CommonResponse<UserResponse> {
        delay(NETWORK_DELAY_MS)
        return ok(mockUser(userTmpCreateRequest.accountType), "User created successfully")
    }

    override suspend fun getUserDefaultFridge(token: String): CommonResponse<FridgeResponse> = ok(mockFridge())

    override suspend fun updateUserSettings(userSettingRequest: UserSettingRequest): CommonResponse<UserSettingResponse> {
        settings = UserSettingResponse(
            isNotification = userSettingRequest.isNotification ?: settings.isNotification,
            useAllIngredients = userSettingRequest.useAllIngredients ?: settings.useAllIngredients,
            autoDeleteFoods = userSettingRequest.autoDeleteFoods ?: settings.autoDeleteFoods
        )
        return ok(settings)
    }

    override suspend fun getUserSettings(): CommonResponse<UserSettingResponse> = ok(settings)

    override suspend fun updateUser(userProfileRequest: UserProfileRequest): CommonResponse<UserProfileResponse> =
        ok(
            UserProfileResponse(
                id = "mock-user",
                username = userProfileRequest.username ?: "목업 사용자",
                accountType = userProfileRequest.accountType ?: "temp",
                notification = userProfileRequest.notification ?: true,
                fcmToken = userProfileRequest.fcmToken.orEmpty(),
                isPremium = false
            )
        )

    override suspend fun getApiUsage(): CommonResponse<ApiUsageResponse> =
        ok(
            ApiUsageResponse(
                recipeUsed = recipeUsed,
                recipeLimit = RECIPE_LIMIT,
                recipeRemaining = RECIPE_LIMIT - recipeUsed,
                receiptUsed = receiptUsed,
                receiptLimit = RECEIPT_LIMIT,
                receiptRemaining = RECEIPT_LIMIT - receiptUsed,
                premium = false
            )
        )

    override suspend fun deleteUser(): CommonResponse<Boolean> = ok(true)

    /* ************************************************************* */
    /* ************************ Oauth API ************************** */
    override suspend fun createUserOnServer(provider: String, userCreateRequest: UserCreateRequest): CommonResponse<UserResponse> =
        ok(mockUser(provider.uppercase()), "User created successfully")

    override suspend fun reissueUserToken(): CommonResponse<TokenResponse> =
        ok(TokenResponse(accessToken = MOCK_TOKEN, refreshToken = MOCK_TOKEN))

    override suspend fun validateUserToken(): CommonResponse<Boolean> = ok(true)

    /* ************************************************************* */
    /* ************************ Recipe API ************************* */
    private fun generateRecipe(names: List<String>, types: List<Int>): RecipeResponse {
        if (recipeUsed >= RECIPE_LIMIT) httpError(429, "Recipe quota exceeded")
        recipeUsed++
        val main = names.take(3).joinToString(" ")
        return RecipeResponse(
            id = UUID.randomUUID().toString(),
            recipeImageUrl = "",
            recipeName = "$main 볶음 (목업)",
            recipeContent = buildString {
                appendLine("1. ${names.joinToString(", ")}을(를) 먹기 좋은 크기로 손질합니다.")
                appendLine("2. 달군 팬에 기름을 두르고 단단한 재료부터 볶습니다.")
                appendLine("3. 간장과 후추로 간을 맞춰 완성합니다.")
            },
            ingredientNames = names,
            ingredientTypes = types,
            createdAt = today().format(serverDate)
        ).also { synchronized(recipes) { recipes.add(0, it) } }
    }

    override suspend fun createRecipe(recipeRequest: RecipeRequest): CommonResponse<RecipeResponse> {
        delay(1500)
        return ok(generateRecipe(recipeRequest.ingredientNames, recipeRequest.ingredientTypes))
    }

    override suspend fun createRecipeFromFridge(): CommonResponse<RecipeResponse> {
        delay(1500)
        val items = synchronized(foods) { foods.sortedBy { it.expiryDate }.take(3) }
        if (items.isEmpty()) httpError(400, "No ingredients in fridge")
        return ok(generateRecipe(items.map { it.itemName.orEmpty() }, items.map { it.categoryId ?: 1 }))
    }

    override suspend fun getRecipes(): CommonResponse<List<RecipeResponse>> {
        delay(NETWORK_DELAY_MS)
        return ok(synchronized(recipes) { recipes.toList() })
    }

    override suspend fun deleteRecipe(recipeId: String): CommonResponse<Boolean> {
        delay(NETWORK_DELAY_MS)
        val removed = synchronized(recipes) { recipes.removeAll { it.id == recipeId } }
        return ok(removed)
    }

    /* ************************************************************* */
    /* ************************ FCM API(TEST) ********************** */
    override suspend fun sendFcmTest(fcmRequest: FcmRequest): CommonResponse<String> = ok("Mock: ${fcmRequest.message}")
}
