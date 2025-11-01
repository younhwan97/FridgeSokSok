package com.yh.fridgesoksok.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

sealed class Resource<T>(val data: T? = null, val message: String? = null, val status: Int? = -1) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, status: Int? = -1) : Resource<T>(data, message, status)
    class Loading<T>(data: T? = null) : Resource<T>(data)
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
        } catch (exception: Exception) {
            val statusCode = if (exception is HttpException) {
                exception.code()
            } else {
                null
            }
            emit(Resource.Error(
                message = exception.localizedMessage ?: "Unknown Error",
                status = statusCode
            ))
        }
    }