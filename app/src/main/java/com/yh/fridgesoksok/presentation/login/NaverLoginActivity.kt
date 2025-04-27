package com.yh.fridgesoksok.presentation.login

import android.app.Activity
import android.os.Bundle
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.yh.fridgesoksok.remote.api.NaverApiService.Companion.deliverResult
import com.yh.fridgesoksok.remote.model.UserResponse

class NaverLoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OAuthLoginCallback 호출 시 결과를 deliver
        val callback = object : OAuthLoginCallback {
            override fun onSuccess() {
                deliverResult(Result.success(
                    UserResponse(
                        id           = 0,
                        accessToken  = NaverIdLoginSDK.getAccessToken(),
                        refreshToken = NaverIdLoginSDK.getRefreshToken(),
                        username     = "",
                        accountType  = "NAVER"
                    )
                ))
                finish()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val code = NaverIdLoginSDK.getLastErrorCode().code
                val desc = NaverIdLoginSDK.getLastErrorDescription()
                deliverResult(Result.failure(
                    RuntimeException("Naver login failed: $httpStatus / $code: $desc")
                ))
                finish()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, callback)
    }
}
