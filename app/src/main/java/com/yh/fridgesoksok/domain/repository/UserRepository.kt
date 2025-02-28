package com.yh.fridgesoksok.domain.repository

interface UserRepository {

    fun getUserToken(): String?
}