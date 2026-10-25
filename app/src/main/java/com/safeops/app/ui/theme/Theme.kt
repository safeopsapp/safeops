package com.safeops.app.ui.theme

import androidx.compose.runtime.Composable
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

@Composable
fun SafeOpsTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SafeOpsTheme(darkTheme = darkTheme, content = content)
}