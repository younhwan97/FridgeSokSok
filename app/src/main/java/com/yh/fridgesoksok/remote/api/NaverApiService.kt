package com.yh.fridgesoksok.remote.api

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.yh.fridgesoksok.presentation.login.NaverLoginActivity
import com.yh.fridgesoksok.remote.model.UserResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

typealias NaverCallback = (Result<UserResponse>) -> Unit

class NaverApiService(
    private val context: Context
) {
    companion object {
        // 액티비티에서 호출할 콜백을 static으로 보관
        private var pendingCallback: NaverCallback? = null

        internal fun deliverResult(result: Result<UserResponse>) {
            pendingCallback?.invoke(result)
            pendingCallback = null
        }
    }

    suspend fun createNaverToken(): UserResponse =
        suspendCancellableCoroutine { cont ->
            // 콜백 저장
            pendingCallback = { result ->
                result.fold(
                    onSuccess = { cont.resume(it) },
                    onFailure = { cont.resumeWithException(it) }
                )
            }

            // NaverLoginActivity로 작업 위임
            val intent = Intent(context, NaverLoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            cont.invokeOnCancellation {
                // 취소 시 정리
                pendingCallback = null
            }
        }
}