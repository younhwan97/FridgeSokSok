package com.yh.fridgesoksok.domain.model

/**
 * 영수증 인식 결과의 보관 기간 근거 (서버 dateBasis)
 */
enum class DateBasis {
    // 제조사가 등록한 소비기한 (가공식품, 제조일 기준)
    LABEL,

    // 냉장 안전 한도 (생육·생선·계란·두부, 구입일 기준)
    SAFETY,

    // 품질 유지 기간 (채소·과일, 냉동 전부)
    QUALITY;

    companion object {
        fun fromValue(value: String?): DateBasis? = entries.firstOrNull { it.name == value }
    }
}
