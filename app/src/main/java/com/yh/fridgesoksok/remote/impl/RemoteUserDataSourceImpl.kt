package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Channel
import com.yh.fridgesoksok.data.model.ApiUsageEntity
import com.yh.fridgesoksok.data.model.FridgeEntity
import com.yh.fridgesoksok.data.model.ServerStatusEntity
import com.yh.fridgesoksok.data.model.TokenEntity
import com.yh.fridgesoksok.data.model.UserEntity
import com.yh.fridgesoksok.data.model.UserSettingEntity
import com.yh.fridgesoksok.data.remote.RemoteUserDataSource
import com.yh.fridgesoksok.remote.api.FridgeApiService
import com.yh.fridgesoksok.remote.api.KakaoApiService
import com.yh.fridgesoksok.remote.api.NaverApiService
import com.yh.fridgesoksok.remote.model.FcmRequest
import com.yh.fridgesoksok.remote.model.UserCreateRequest
import com.yh.fridgesoksok.remote.model.UserProfileRequest
import com.yh.fridgesoksok.remote.model.UserSettingRequest
import com.yh.fridgesoksok.remote.model.toEntity
import javax.inject.Inject

private const val TAG = "RemoteUserData"
private const val FCM_TEST_TITLE = "FridgeSokSok"

class RemoteUserDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService,
    private val naverApiService: NaverApiService,
    private val fridgeApiService: FridgeApiService
) : RemoteUserDataSource {

    override suspend fun getServerStatus(): ServerStatusEntity =
        apiCall(TAG, "getServerStatus") {
            fridgeApiService.getServerStatus().requireData("getServerStatus").toEntity()
        }

    override suspend fun createUserOnChannel(channel: Channel): UserEntity =
        apiCall(TAG, "createUserOnChannel", channel) {
            val data = when (channel) {
                Channel.KAKAO -> kakaoApiService.createUserOnKakao()
                Channel.NAVER -> naverApiService.createUserOnNaver()
                Channel.GUEST -> fridgeApiService.createTmpUser().data
            } ?: throw IllegalStateException("createUserOnChannel data(=null)")
            data.toEntity()
        }

    override suspend fun createUserOnServer(user: UserEntity): UserEntity =
        apiCall(TAG, "createUserOnServer", user.accountType) {
            val provider = when (user.accountType) {
                Channel.KAKAO.toString() -> "kakao"
                Channel.NAVER.toString() -> "naver"
                else -> throw IllegalStateException("createUserOnServer unsupported accountType(=${user.accountType})")
            }
            fridgeApiService.createUserOnServer(
                provider = provider,
                userCreateRequest = UserCreateRequest(user.accessToken)
            ).requireData("createUserOnServer").toEntity()
        }

    override suspend fun deleteUser(): Boolean =
        apiCall(TAG, "deleteUser") {
            fridgeApiService.deleteUser().requireData("deleteUser")
        }

    // refreshToken 헤더는 RequestHeaderInterceptor 에서 주입
    override suspend fun validateUserToken(refreshToken: String): Boolean =
        apiCall(TAG, "validateUserToken") {
            fridgeApiService.validateUserToken().requireData("validateUserToken")
        }

    // refreshToken 헤더는 RequestHeaderInterceptor 에서 주입
    override suspend fun reissueUserToken(refreshToken: String): TokenEntity =
        apiCall(TAG, "reissueUserToken") {
            fridgeApiService.reissueUserToken().requireData("reissueUserToken").toEntity()
        }

    override suspend fun getUserDefaultFridge(accessToken: String): FridgeEntity =
        apiCall(TAG, "getUserDefaultFridge") {
            fridgeApiService.getUserDefaultFridge("Bearer $accessToken")
                .requireData("getUserDefaultFridge").toEntity()
        }

    override suspend fun updateReceiveNotification(enabled: Boolean): Boolean =
        apiCall(TAG, "updateReceiveNotification", enabled) {
            fridgeApiService.updateUserSettings(UserSettingRequest(isNotification = enabled, null, null))
                .data?.isNotification ?: throw IllegalStateException("updateReceiveNotification data(=null)")
        }

    override suspend fun updateAutoDeleteExpired(enabled: Boolean): Boolean =
        apiCall(TAG, "updateAutoDeleteExpired", enabled) {
            fridgeApiService.updateUserSettings(UserSettingRequest(null, null, autoDeleteFoods = enabled))
                .data?.autoDeleteFoods ?: throw IllegalStateException("updateAutoDeleteExpired data(=null)")
        }

    override suspend fun updateUseAllIngredients(enabled: Boolean): Boolean =
        apiCall(TAG, "updateUseAllIngredients", enabled) {
            fridgeApiService.updateUserSettings(UserSettingRequest(null, useAllIngredients = enabled, null))
                .data?.useAllIngredients ?: throw IllegalStateException("updateUseAllIngredients data(=null)")
        }

    override suspend fun getUserSettings(): UserSettingEntity =
        apiCall(TAG, "getUserSettings") {
            fridgeApiService.getUserSettings().requireData("getUserSettings").toEntity()
        }

    override suspend fun updateUserFcmToken(fcmToken: String): String =
        apiCall(TAG, "updateUserFcmToken") {
            fridgeApiService.updateUser(UserProfileRequest(null, null, null, fcmToken, null, null))
                .data?.fcmToken ?: throw IllegalStateException("updateUserFcmToken data(=null)")
        }

    override suspend fun sendMessage(message: String): String =
        apiCall(TAG, "sendMessage", message) {
            fridgeApiService.sendFcmTest(FcmRequest(title = FCM_TEST_TITLE, body = message)).requireData("sendMessage")
        }

    override suspend fun getApiUsage(): List<ApiUsageEntity> =
        apiCall(TAG, "getApiUsage") {
            fridgeApiService.getApiUsage().requireData("getApiUsage").toEntity()
        }
}
