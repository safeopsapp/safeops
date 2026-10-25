package com.safeops.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MaterialLightColorScheme = lightColorScheme(
    primary = RoyalBluePrimary,
    onPrimary = SurfaceLight,
    primaryContainer = RoyalBlueLight,
    onPrimaryContainer = RoyalBlueDark,
    secondary = DeepNavy,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    onBackground = PrimaryTextLight,
    surface = SurfaceLight,
    onSurface = PrimaryTextLight,
    surfaceVariant = SecondarySurfaceLight,
    onSurfaceVariant = SecondaryTextLight,
    outline = BorderLight,
    error = DangerRed,
    onError = SurfaceLight
)

private val MaterialDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlue,
    onPrimary = SurfaceLight,
    primaryContainer = DarkElevatedSurface,
    onPrimaryContainer = DarkPrimaryText,
    secondary = DarkSecondaryText,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    error = DangerRed,
    onError = SurfaceLight
)

@Composable
fun SafeOpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val safeOpsColors = if (darkTheme) DarkSafeOpsColorScheme else LightSafeOpsColorScheme
    val safeOpsTypography = SafeOpsTypography()
    val safeOpsSpacing = SafeOpsSpacing()
    val safeOpsShapes = SafeOpsShapes()
    val safeOpsElevation = SafeOpsElevation()

    val materialColors = if (darkTheme) MaterialDarkColorScheme else MaterialLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSafeOpsColors provides safeOpsColors,
        LocalSafeOpsTypography provides safeOpsTypography,
        LocalSafeOpsSpacing provides safeOpsSpacing,
        LocalSafeOpsShapes provides safeOpsShapes,
        LocalSafeOpsElevation provides safeOpsElevation
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}

object SafeOpsTheme {
    val colors: SafeOpsColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalSafeOpsColors.current

    val typography: SafeOpsTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalSafeOpsTypography.current

    val spacing: SafeOpsSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSafeOpsSpacing.current

    val shapes: SafeOpsShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalSafeOpsShapes.current

    val elevation: SafeOpsElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalSafeOpsElevation.current
}
