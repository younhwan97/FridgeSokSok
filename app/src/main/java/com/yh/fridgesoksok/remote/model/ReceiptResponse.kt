package com.yh.fridgesoksok.remote.model

import com.yh.fridgesoksok.data.model.ReceiptEntity
import com.yh.fridgesoksok.remote.RemoteMapper

data class ReceiptResponse(
    val name: String,
    val count: Int
) : RemoteMapper<ReceiptEntity> {
    override fun toData(): ReceiptEntity =
        ReceiptEntity(name, count)
}
