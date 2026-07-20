package com.example.menurestoran.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.material3.LocalRippleConfiguration
import com.example.menurestoran.ui.utils.LocalReduceMotion
import com.example.menurestoran.ui.utils.isReduceMotionEnabled
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RonaDarkTerracotta,
    onPrimary = RonaCream,
    secondary = RonaDarkGold,
    onSecondary = RonaCharcoal,
    background = RonaDarkBackground,
    onBackground = RonaDarkOnSurface,
    surface = RonaDarkSurface,
    onSurface = RonaDarkOnSurface,
    tertiary = RonaSage,
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = RonaTerracotta,
    onPrimary = RonaCream,
    secondary = RonaGold,
    onSecondary = Color.White,
    background = RonaParchment,
    onBackground = RonaCharcoal,
    surface = RonaSurface,
    onSurface = RonaCharcoal,
    tertiary = RonaSage,
    onTertiary = Color.White,
    error = Color(0xFFB00020)
)

@Composable
fun RonaRasaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val reduceMotion = isReduceMotionEnabled()
    
    val rippleConfig = RippleConfiguration(
        color = RonaGold.copy(alpha = 0.2f)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalRippleConfiguration provides rippleConfig,
        LocalReduceMotion provides reduceMotion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = RonaShapes,
            content = content
        )
    }
}
