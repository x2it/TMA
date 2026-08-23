package com.realtor.geeksales.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Mono = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 30.sp, letterSpacing = (-0.5).sp, color = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, letterSpacing = (-0.25).sp, color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, color = TextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 15.sp, color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontSize = 15.sp, color = TextPrimary, lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp, color = TextSecondary, lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, letterSpacing = 0.3.sp, color = TextSecondary
    )
)
