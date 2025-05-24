package com.yh.fridgesoksok.common

import android.util.Log
import com.yh.fridgesoksok.BuildConfig
import retrofit2.HttpException

object Logger {

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            val errorBody = (throwable as? HttpException)?.response()?.errorBody()?.string()
            val fullMessage = buildString {
                append(message)
                throwable?.localizedMessage?.let { append("\n→ $it") }
                errorBody?.let { append("\n→ Body: $it") }
            }
            Log.e(tag, fullMessage)
        }
    }
}