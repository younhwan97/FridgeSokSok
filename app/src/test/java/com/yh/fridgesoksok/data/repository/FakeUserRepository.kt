package com.yh.fridgesoksok.data.repository

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.common.Resource
import com.yh.fridgesoksok.domain.model.ApiUsage
import com.yh.fridgesoksok.domain.model.Fridge
import com.yh.fridgesoksok.domain.model.ServerStatus
import com.yh.fridgesoksok.domain.model.Token
import com.yh.fridgesoksok.domain.model.User
import com.yh.fridgesoksok.domain.model.UserSetting
import com.yh.fridgesoksok.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FakeUserRepository : UserRepository {

    private var currentUser: User? = null
    private var userSetting = UserSetting(
        receiveNotification = true,
        useAllIngredients = false,
        autoDeleteExpiredFoods = false
    )
    private var fcmToken: String = ""
    private val apiUsages = mutableListOf<ApiUsage>()
    private val messages = mutableListOf<String>()

    override fun getServerStatus(): Flow<Resource<ServerStatus>> {
        val status = ServerStatus(
            version = "1.0.0",
            status = "running",
            time = "2025-01-01T00:00:00",
            startTime = "2025-01-01T00:00:00"
        )
        return flow { emit(Resource.Success(status)) }
    }

    override fun loadUser(): User {
        return currentUser ?: User(
            id = "",
            accessToken = "",
            refreshToken = "",
            username = "",
            accountType = "",
            defaultFridgeId = ""
        )
    }

    override fun saveUser(user: User): Flow<Resource<Boolean>> {
        currentUser = user
        return flow { emit(Resource.Success(true)) }
    }

    override fun updateUser(user: User): Flow<Resource<Boolean>> {
        currentUser = user
        return flow { emit(Resource.Success(true)) }
    }

    override fun clearUser(): Flow<Resource<Boolean>> {
        currentUser = null
        return flow { emit(Resource.Success(true)) }
    }

    override fun deleteUser(): Flow<Resource<Boolean>> {
        currentUser = null
        return flow { emit(Resource.Success(true)) }
    }

    override fun createUserOnChannel(channel: Channel): Flow<Resource<User>> {
        val user = User(
            id = UUID.randomUUID().toString(),
            accessToken = "test-access-token",
            refreshToken = "test-refresh-token",
            username = "Test User",
            accountType = channel.name,
            defaultFridgeId = UUID.randomUUID().toString()
        )
        currentUser = user
        return flow { emit(Resource.Success(user)) }
    }

    override fun createUserOnServer(user: User): Flow<Resource<User>> {
        currentUser = user
        return flow { emit(Resource.Success(user)) }
    }

    override fun validateUserToken(refreshToken: String): Flow<Resource<Boolean>> {
        val isValid = refreshToken.isNotEmpty()
        return flow { emit(Resource.Success(isValid)) }
    }

    override fun reissueUserToken(refreshToken: String): Flow<Resource<Token>> {
        val token = Token(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token"
        )
        return flow { emit(Resource.Success(token)) }
    }

    override fun getUserDefaultFridge(accessToken: String): Flow<Resource<Fridge>> {
        val fridge = Fridge(
            id = "fridge-1",
            fridgeName = "My Fridge",
            createdAt = "2025-01-01",
            foodProducts = emptyList()
        )
        return flow { emit(Resource.Success(fridge)) }
    }

    override fun getUserSetting(): Flow<Resource<UserSetting>> {
        return flow { emit(Resource.Success(userSetting)) }
    }

    override fun updateReceiveNotification(enabled: Boolean): Flow<Resource<Boolean>> {
        userSetting = userSetting.copy(receiveNotification = enabled)
        return flow { emit(Resource.Success(true)) }
    }

    override fun updateAutoDeleteExpired(enabled: Boolean): Flow<Resource<Boolean>> {
        userSetting = userSetting.copy(autoDeleteExpiredFoods = enabled)
        return flow { emit(Resource.Success(true)) }
    }

    override fun updateUseAllIngredients(enabled: Boolean): Flow<Resource<Boolean>> {
        userSetting = userSetting.copy(useAllIngredients = enabled)
        return flow { emit(Resource.Success(true)) }
    }

    override fun updateUserFcmToken(fcmToken: String): Flow<Resource<String>> {
        this.fcmToken = fcmToken
        return flow { emit(Resource.Success(fcmToken)) }
    }

    override fun updateLocalUserFcmToken(fcmToken: String): Flow<Resource<Boolean>> {
        this.fcmToken = fcmToken
        return flow { emit(Resource.Success(true)) }
    }

    override fun getLocalUserFcmToken(): String {
        return fcmToken
    }

    override fun sendMessage(message: String): Flow<Resource<String>> {
        messages.add(message)
        return flow { emit(Resource.Success("Message sent: $message")) }
    }

    override fun getApiUsage(): Flow<Resource<List<ApiUsage>>> {
        return flow { emit(Resource.Success(apiUsages.toList())) }
    }

    override fun getLocalApiUsage(feature: String): ApiUsage {
        return apiUsages.find { it.feature == feature } ?: ApiUsage(
            feature = feature,
            max = 100,
            used = 0
        )
    }

    override fun updateApiUsage(apiUsages: List<ApiUsage>): Flow<Resource<Boolean>> {
        this.apiUsages.clear()
        this.apiUsages.addAll(apiUsages)
        return flow { emit(Resource.Success(true)) }
    }
}
