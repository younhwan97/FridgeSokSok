package com.yh.fridgesoksok.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yh.fridgesoksok.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle( // Heading 1 - 24px Semibold
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle( // Heading 2 - 18px Medium
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    bodyLarge = TextStyle( // 대표: Body Large_M
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle( // 대표: Body Medium_M
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle( // 대표: Body Small_M
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)