package com.yh.fridgesoksok.presentation.common.constants

/**
 * 네비게이션 관련 상수
 */
object NavigationConstants {
    // SavedStateHandle 키
    const val KEY_FOOD = "food"
    const val KEY_SOURCE = "source"
    const val KEY_EDITED_FOOD = "edited_food"
    // Upload → Home: 추가한 식품 개수 (냉장고 화면에서 완료 스낵바 표시)
    const val KEY_ADDED_FOOD_COUNT = "added_food_count"

    // 네비게이션 출처
    const val SOURCE_HOME = "HOME"
    const val SOURCE_UPLOAD = "UPLOAD"

    // 기타
    const val MIN_COUNT = 1
}
