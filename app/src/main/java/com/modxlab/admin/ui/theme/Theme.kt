package com.modxlab.admin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OrganicLightColorScheme = lightColorScheme(
    primary = BrandSage,
    onPrimary = Color.White,
    primaryContainer = AppSurface,
    onPrimaryContainer = TextPrimary,
    secondary = BrandSageLight,
    onSecondary = TextPrimary,
    secondaryContainer = AppSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = BrandIndigo,
    onTertiary = Color.White,
    background = AppBg,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AppBorder,
    error = BrandCrimson,
    onError = Color.White
)

@Composable
fun ModXAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = OrganicLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

