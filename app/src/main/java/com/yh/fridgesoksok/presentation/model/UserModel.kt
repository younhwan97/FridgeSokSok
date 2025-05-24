package com.yh.fridgesoksok.presentation.model

import com.yh.fridgesoksok.domain.model.User

/*
1. 객체지향적 책임이 있는 모델
    → 내부에 함수(메서드) 구현 가능하고 적절
    ⦁ 도메인 로직을 수행하거나 상태를 변화시킬 수 있는 모델
    ⦁ 그 자체로 "비즈니스 로직"의 핵심을 이루는 객체
    ⦁ 예: Money.add(), Order.submit(), User.isTokenValid()
2. 순수 DTO/Entity/Response 모델
    → 내부에 함수 넣지 말고 외부 확장 함수로 분리
    ⦁ 목적이 순수한 데이터 전달 또는 저장
    ⦁ 로직이나 의미 있는 행위가 없음
    ⦁ Remote, Local, API 연동에 최적화된 구조체일 뿐
    ⦁ 바뀔 일이 많고, 외부 스펙에 종속됨
*/

data class UserModel private constructor(
    val id: String,
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
                accessToken = user.accessToken,
                refreshToken = user.refreshToken,
                username = user.username,
                accountType = user.accountType,
                defaultFridgeId = user.defaultFridgeId
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
    User(id, accessToken, refreshToken, username, accountType, defaultFridgeId)