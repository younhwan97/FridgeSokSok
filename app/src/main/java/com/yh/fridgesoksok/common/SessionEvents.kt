package com.yh.fridgesoksok.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션(리프레시 토큰) 만료 이벤트
 * TokenAuthenticator 가 재발급을 거부당하면 발행하고, MainActivity 가 구독해 로그인 화면으로 이동한다.
 */
@Singleton
class SessionEvents @Inject constructor() {

    private val _expired = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val expired = _expired.asSharedFlow()

    fun notifyExpired() {
        _expired.tryEmit(Unit)
    }
}
