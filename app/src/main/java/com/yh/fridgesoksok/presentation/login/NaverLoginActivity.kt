package com.yh.fridgesoksok.presentation.login

import android.app.Activity
import android.os.Bundle
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class NaverLoginActivity : Activity() {
    companion object {
        internal var pendingCallback: OAuthLoginCallback? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onSuccess() {
                pendingCallback?.onSuccess()
                pendingCallback = null
                finish()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                pendingCallback?.onFailure(httpStatus, message)
                pendingCallback = null
                finish()
            }
            override fun onError(errorCode: Int, message: String) {
                pendingCallback?.onError(errorCode, message)
                pendingCallback = null
                finish()
            }
        })
    }
}