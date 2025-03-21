package com.yh.fridgesoksok.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yh.fridgesoksok.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendard_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendard_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.pretendard_thin, FontWeight.Thin, FontStyle.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = CustomSubDarkTextColor
    ),
    bodyMedium = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = CustomDarkTextColor
    ),
    bodyLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CustomDarkTextColor
    ),
    headlineLarge = TextStyle(
        fontFamily = Pretendard, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, color = CustomDarkTextColor
    )
)