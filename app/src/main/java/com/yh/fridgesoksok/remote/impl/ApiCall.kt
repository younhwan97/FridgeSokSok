package com.yh.fridgesoksok.remote.impl

import com.yh.fridgesoksok.common.Logger
import com.yh.fridgesoksok.remote.model.CommonResponse

/**
 * API 호출 공통 처리 (요청 로그 + 실패 로그 후 예외 전파)
 * 토큰 등 민감 정보는 input 에 넘기지 않는다.
 */
internal suspend inline fun <T> apiCall(
    tag: String,
    name: String,
    input: Any? = null,
    crossinline block: suspend () -> T
): T =
    try {
        Logger.d(tag, if (input == null) name else "$name INPUT $input")
        block()
    } catch (e: Exception) {
        Logger.e(tag, "$name 실패", e)
        throw e
    }

internal fun <T> CommonResponse<T>.requireData(name: String): T =
    data ?: throw IllegalStateException("$name data(=null)")
