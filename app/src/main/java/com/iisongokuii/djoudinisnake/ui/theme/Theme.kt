package com.iisongokuii.djoudinisnake.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DjoudiniColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonBlue,
    tertiary = NeonGreen,
    background = DeepSpace,
    surface = DarkGrey,
    onBackground = NeonBlue,
    onSurface = NeonBlue,
    error = BloodRed
)

@Composable
fun DjoudinisSnakeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DjoudiniColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepSpace.toArgb()
            window.navigationBarColor = DeepSpace.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.setDecorFitsSystemWindows(window, false) // Edge to edge!
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
