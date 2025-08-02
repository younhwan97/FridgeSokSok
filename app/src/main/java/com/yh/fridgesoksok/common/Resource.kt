package com.yh.fridgesoksok.common

sealed class Resource<T>(val data: T? = null, val message: String? = null, val status: Int? = -1) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, status: Int? = -1) : Resource<T>(data, message, status)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}