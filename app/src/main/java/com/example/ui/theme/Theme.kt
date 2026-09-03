package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NothingWhite,
    onPrimary = NothingBlack,
    primaryContainer = NothingSurfaceElevated,
    onPrimaryContainer = NothingWhite,
    secondary = NothingGray,
    onSecondary = NothingWhite,
    secondaryContainer = NothingSurfaceDark,
    onSecondaryContainer = NothingOffWhite,
    tertiary = NothingRed,
    onTertiary = NothingWhite,
    background = NothingBlack,
    onBackground = NothingWhite,
    surface = NothingSurfaceDark,
    onSurface = NothingWhite,
    surfaceVariant = NothingBorderDark,
    onSurfaceVariant = NothingLightGray,
    outline = NothingBorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = NothingLightTextPrimary,
    onPrimary = NothingWhite,
    primaryContainer = NothingLightSurface,
    onPrimaryContainer = NothingLightTextPrimary,
    secondary = NothingLightTextSecondary,
    onSecondary = NothingWhite,
    secondaryContainer = NothingLightBackground,
    onSecondaryContainer = NothingLightTextPrimary,
    tertiary = NothingRed,
    onTertiary = NothingWhite,
    background = NothingLightBackground,
    onBackground = NothingLightTextPrimary,
    surface = NothingLightSurface,
    onSurface = NothingLightTextPrimary,
    surfaceVariant = NothingLightBorder,
    onSurfaceVariant = NothingLightTextSecondary,
    outline = NothingLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enables Nothing OS 5.0 Adaptive Color Engine
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
