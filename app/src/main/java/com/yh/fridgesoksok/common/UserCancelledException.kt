package com.yh.fridgesoksok.common

/**
 * 사용자가 소셜 로그인을 직접 취소함 (오류 안내 대상이 아님)
 */
class UserCancelledException : Exception("사용자가 로그인을 취소했습니다")
