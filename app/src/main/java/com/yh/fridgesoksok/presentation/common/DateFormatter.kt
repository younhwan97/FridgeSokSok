package com.yh.fridgesoksok.presentation.common

import java.time.format.DateTimeFormatter

object DateFormatter {
    val yyyyMMdd: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val yyyyMMddDot: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
}