package com.yh.fridgesoksok.data

internal interface DataMapper<DomainModel> {
    fun toDomain(): DomainModel
}