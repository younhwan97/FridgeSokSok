package com.yh.fridgesoksok.presentation.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateFormatter {
    val yyyyMMdd: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val yyyyMMddDot: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
}

fun String.toFormattedDate(outputPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")): String {
    // 시도할 포맷 리스트
    val inputFormats = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,          // 2024-06-08T10:44:52.446101Z
        DateTimeFormatter.ISO_ZONED_DATE_TIME,           // 2024-06-08T10:44:52.446101+09:00[Asia/Seoul]
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,           // 2024-06-08T10:44:52.446101
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    )

    for (formatter in inputFormats) {
        try {
            // 타입 감지 및 변환
            val temporal = formatter.parse(this)

            return try {
                OffsetDateTime.from(temporal).format(outputPattern)
            } catch (_: Exception) {
                try {
                    ZonedDateTime.from(temporal).format(outputPattern)
                } catch (_: Exception) {
                    try {
                        LocalDateTime.from(temporal).format(outputPattern)
                    } catch (_: Exception) {
                        try {
                            LocalDate.from(temporal).format(outputPattern)
                        } catch (_: Exception) {
                            this // 파싱은 되지만 어떤 타입도 아님
                        }
                    }
                }
            }
        } catch (e: DateTimeParseException) {
            // skip to next format
        }
    }

    return this // 모든 시도 실패 시 원본 반환
}