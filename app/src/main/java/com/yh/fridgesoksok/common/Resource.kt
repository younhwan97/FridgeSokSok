package com.yh.fridgesoksok.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

sealed class Resource<T>(val data: T? = null, val message: String? = null, val status: Int? = -1) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, status: Int? = -1) : Resource<T>(data, message, status)
    class Loading<T>(data: T? = null) : Resource<T>(data)

    companion object {
        // HTTP 응답이 없는 실패를 구분하기 위한 상태값 (HTTP 상태 코드와 겹치지 않는 음수)
        const val STATUS_NETWORK_ERROR = -2
        const val STATUS_CANCELLED = -3
    }
}

/**
 * 비동기 작업을 Resource로 감싸는 Flow를 생성합니다.
 *
 * @param block 비동기로 실행할 작업
 * @return Flow<Resource<T>> - Loading → Success 또는 Error 상태를 방출하는 Flow
 */
inline fun <T> flowWithResource(crossinline block: suspend () -> T): Flow<Resource<T>> =
    flow {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(block()))
        } catch (exception: CancellationException) {
            // 취소는 실패가 아니므로 그대로 전파
            throw exception
        } catch (exception: Exception) {
            val statusCode = when (exception) {
                is HttpException -> exception.code()
                is IOException -> Resource.STATUS_NETWORK_ERROR
                is UserCancelledException -> Resource.STATUS_CANCELLED
                else -> null
            }
            emit(Resource.Error(
                message = exception.localizedMessage ?: "Unknown Error",
                status = statusCode
            ))
        }
    }
