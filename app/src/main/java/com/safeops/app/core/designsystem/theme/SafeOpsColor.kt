package com.safeops.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val iTantraPrimaryNavy = Color(0xFF06233F)
val iTantraPrimaryBlue = Color(0xFF2979FF)
val iTantraMissionOrange = Color(0xFFF58220)

// SafeOps Brand Tokens - User Specified Light Theme Palette
val RoyalBluePrimary = Color(0xFF1769FF)
val RoyalBlueDark = Color(0xFF0D47C9)
val RoyalBlueLight = Color(0xFFEAF2FF)
val DeepNavy = Color(0xFF0B1F4D)

val LightBackground = Color(0xFFF5F4FA)
val LightSurface = Color(0xFFFFFDFF)
val LightPrimaryText = Color(0xFF18181D)
val LightSecondaryText = Color(0xFF77767E)
val LightOutline = Color(0xFFE4E2E9)
val LightAccentContainer = Color(0xFFE3EFFF)
val LightSuccess = Color(0xFF16A34A)
val LightWarning = Color(0xFFF59E0B)
val LightDangerRed = Color(0xFFDC2626)
val AccentOrange = Color(0xFFFF8A00)

// Aliases for legacy component compatibility
val SurfaceLight = LightSurface
val BackgroundLight = LightBackground
val PrimaryTextLight = LightPrimaryText
val SecondaryTextLight = LightSecondaryText
val SecondarySurfaceLight = LightSurface
val BorderLight = LightOutline
val DisabledColorLight = Color(0xFF9AA6BA)
val SuccessGreen = LightSuccess
val WarningAmber = LightWarning
val DangerRed = LightDangerRed
val BrightBlueAccent = Color(0xFF2AA7FF)

// SafeOps Brand Tokens - User Specified Dark Theme Palette (Glass Black UI)
val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF18181B)
val DarkPrimaryText = Color(0xFFE8E6EC)
val DarkSecondaryText = Color(0xFFA6A3AA)
val DarkOutline = Color(0xFF333338)
val DarkAccentContainer = Color(0xFF1E293B)
val DarkSuccess = Color(0xFF34D399)
val DarkWarning = Color(0xFFFBBF24)
val DarkPrimaryBlue = Color(0xFF0878FF)
val DarkDangerRed = Color(0xFFEF4444)

val DarkElevatedSurface = DarkAccentContainer
val DarkBorder = DarkOutline
val DarkDisabledColor = Color(0xFF475569)

// Gradients (Premium Mixed Light and Dark Blue Combination)
val SafeOpsHeroGradient = Brush.verticalGradient(
    0.00f to Color(0xFF38BDF8), // Radiant light blue accent at top
    0.14f to Color(0xFF1E88E5), // Vibrant azure blue
    0.38f to Color(0xFF0D47A1), // Deep royal blue
    0.70f to Color(0xFF062B66), // Rich dark navy
    1.00f to Color(0xFF020D22)  // Dark midnight blue base
)

val SafeOpsHeroGradientHorizontal = Brush.horizontalGradient(
    0.00f to Color(0xFF1E88E5), // Light azure blue
    0.45f to Color(0xFF0D47A1), // Royal blue
    1.00f to Color(0xFF031535)  // Dark midnight navy
)

val SafeOpsDarkHeroGradient = Brush.verticalGradient(
    0.00f to Color(0xFF1E88E5), // Luminous light-blue accent at top
    0.16f to Color(0xFF0D47A1), // Rich royal blue
    0.46f to Color(0xFF08265E), // Deep cobalt navy
    0.78f to Color(0xFF031336), // Dark midnight blue
    1.00f to Color(0xFF010A1A)  // Darkest pure sapphire base
)

val SafeOpsButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF0878FF),
        Color(0xFF005AE0)
    )
)

@Immutable
data class SafeOpsColorScheme(
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val secondarySurface: Color,
    val elevatedSurface: Color,
    val border: Color,
    val borderSubtle: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val tertiaryText: Color,
    val disabled: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val danger: Color,
    val onDanger: Color,
    val accentOrange: Color,
    val heroGradient: Brush,
    val buttonGradient: Brush,
    val cardGlassTint: Color,
    val isDark: Boolean
)

val LightSafeOpsColorScheme = SafeOpsColorScheme(
    primary = RoyalBluePrimary,
    primaryDark = RoyalBlueDark,
    primaryLight = LightAccentContainer,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightSurface,
    onSurface = LightPrimaryText,
    secondarySurface = LightSurface,
    elevatedSurface = LightSurface,
    border = LightOutline,
    borderSubtle = LightOutline.copy(alpha = 0.6f),
    primaryText = LightPrimaryText,
    secondaryText = LightSecondaryText,
    tertiaryText = LightSecondaryText.copy(alpha = 0.7f),
    disabled = LightSecondaryText.copy(alpha = 0.5f),
    success = LightSuccess,
    onSuccess = Color.White,
    warning = LightWarning,
    onWarning = Color.White,
    danger = LightDangerRed,
    onDanger = Color.White,
    accentOrange = AccentOrange,
    heroGradient = SafeOpsHeroGradient,
    buttonGradient = SafeOpsButtonGradient,
    cardGlassTint = LightSurface.copy(alpha = 0.95f),
    isDark = false
)

val DarkSafeOpsColorScheme = SafeOpsColorScheme(
    primary = DarkPrimaryBlue,
    primaryDark = RoyalBlueDark,
    primaryLight = DarkAccentContainer,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = DarkPrimaryText,
    surface = DarkSurface,
    onSurface = DarkPrimaryText,
    secondarySurface = DarkAccentContainer,
    elevatedSurface = DarkAccentContainer,
    border = DarkOutline,
    borderSubtle = DarkOutline.copy(alpha = 0.6f),
    primaryText = DarkPrimaryText,
    secondaryText = DarkSecondaryText,
    tertiaryText = DarkSecondaryText.copy(alpha = 0.7f),
    disabled = DarkSecondaryText.copy(alpha = 0.5f),
    success = DarkSuccess,
    onSuccess = Color.Black,
    warning = DarkWarning,
    onWarning = Color.Black,
    danger = DarkDangerRed,
    onDanger = Color.White,
    accentOrange = AccentOrange,
    heroGradient = SafeOpsDarkHeroGradient,
    buttonGradient = SafeOpsButtonGradient,
    cardGlassTint = DarkSurface.copy(alpha = 0.85f),
    isDark = true
)

val LocalSafeOpsColors = staticCompositionLocalOf { LightSafeOpsColorScheme }
