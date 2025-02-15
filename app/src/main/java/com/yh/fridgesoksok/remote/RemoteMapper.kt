package com.yh.fridgesoksok.remote

internal interface RemoteMapper<DataModel> {
    fun toData(): DataModel
}