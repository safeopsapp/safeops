package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * Standard Loading Component for SafeOps.
 */
@Composable
fun SafeOpsLoading(
    modifier: Modifier = Modifier,
    message: String? = null,
    size: Dp = 40.dp,
    color: Color = SafeOpsTheme.colors.primary
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = color,
            strokeWidth = 3.dp
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = SafeOpsTheme.typography.secondaryMedium.copy(
                    color = SafeOpsTheme.colors.secondaryText,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
fun SafeOpsFullScreenLoading(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SafeOpsTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        SafeOpsLoading(message = message)
    }
}
