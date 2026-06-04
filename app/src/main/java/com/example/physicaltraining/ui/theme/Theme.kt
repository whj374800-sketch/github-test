package com.example.physicaltraining.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TrainingOrange,
    onPrimary = TrainingBlack,
    primaryContainer = TrainingOrangeDark,
    onPrimaryContainer = TrainingText,
    secondary = TrainingTextMuted,
    onSecondary = TrainingBlack,
    tertiary = TrainingOrange,
    background = TrainingBlack,
    onBackground = TrainingText,
    surface = TrainingSurface,
    onSurface = TrainingText,
    surfaceVariant = TrainingSurfaceHigh,
    onSurfaceVariant = TrainingTextMuted,
    outline = TrainingOutline,
    outlineVariant = TrainingOutline,
    error = androidx.compose.ui.graphics.Color(0xFFFF6B4A)
)

private val LightColorScheme = lightColorScheme(
    primary = TrainingOrange,
    onPrimary = TrainingBlack,
    primaryContainer = TrainingOrangeDark,
    onPrimaryContainer = TrainingText,
    secondary = TrainingTextMuted,
    onSecondary = TrainingBlack,
    tertiary = TrainingOrange,
    background = TrainingBlack,
    onBackground = TrainingText,
    surface = TrainingSurface,
    onSurface = TrainingText,
    surfaceVariant = TrainingSurfaceHigh,
    onSurfaceVariant = TrainingTextMuted,
    outline = TrainingOutline,
    outlineVariant = TrainingOutline,
    error = androidx.compose.ui.graphics.Color(0xFFFF6B4A)
)

private val TrainingShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(2.dp),
    medium = RoundedCornerShape(3.dp),
    large = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(4.dp)
)

@Composable
fun PhysicalTrainingTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    val context = LocalContext.current
    if (context is Activity) {
        SideEffect {
            context.window.statusBarColor = colorScheme.background.toArgb()
            context.window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(context.window, context.window.decorView)
                .isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(context.window, context.window.decorView)
                .isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = TrainingShapes
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background,
            content = content
        )
    }
}
