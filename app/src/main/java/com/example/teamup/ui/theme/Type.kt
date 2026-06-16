package com.example.teamup.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.teamup.R

// Set of Material typography styles to start with

val SofiaSans = FontFamily (
    Font(R.font.sofiasans_semibold), Font(R.font.sofiasans_bold)
)

val Typography = Typography(
    displayLarge = TextStyle (
        fontFamily = SofiaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle (
        fontFamily = SofiaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle (
        fontFamily = SofiaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle (
        fontFamily = SofiaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)