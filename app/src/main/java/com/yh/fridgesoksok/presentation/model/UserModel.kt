package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.User

data class UserModel private constructor(
    val id: Long,
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val accountType: String,
    val defaultFridgeId: String
) {
    companion object {
        fun fromLocal(user: User): UserModel {
            return UserModel(
                id = user.id,
                accessToken = user.accessToken.orEmpty(),
                refreshToken = user.refreshToken.orEmpty(),
                username = user.username.orEmpty(),
                accountType = user.accountType.orEmpty(),
                defaultFridgeId = ""
            )
        }
    }

    fun isValid(): Boolean =
        accessToken.isNotBlank() && refreshToken.isNotBlank()

    fun withReissuedToken(newAccess: String, newRefresh: String): UserModel =
        copy(accessToken = newAccess, refreshToken = newRefresh)

    fun withDefaultFridgeId(id: String): UserModel =
        copy(defaultFridgeId = id)

    fun withUpdatedUserInfo(newAccess: String, newRefresh: String, newName: String, newAccountType: String): UserModel =
        copy(accessToken = newAccess, refreshToken = newRefresh, username = newName, accountType = newAccountType)
}

// Mapper
fun User.toPresentation() =
    UserModel.fromLocal(this)

fun UserModel.toDomain() =
    User(id, accessToken, refreshToken, username, accountType)