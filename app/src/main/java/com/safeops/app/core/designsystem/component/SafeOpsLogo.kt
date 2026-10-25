package com.safeops.app.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.core.designsystem.theme.SafeOpsTheme

/**
 * SafeOps Official Emblem (Royal Blue Shield + Construction Hardhat)
 */
@Composable
fun SafeOpsShieldEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    shieldColor: Color = RoyalBluePrimary,
    hardhatColor: Color = AccentOrange,
    borderColor: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Draw Shield Path
        val shieldPath = Path().apply {
            moveTo(w * 0.15f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.15f)
            cubicTo(
                w * 0.88f, h * 0.45f,
                w * 0.80f, h * 0.72f,
                w * 0.50f, h * 0.92f
            )
            cubicTo(
                w * 0.20f, h * 0.72f,
                w * 0.12f, h * 0.45f,
                w * 0.15f, h * 0.15f
            )
            close()
        }

        // Fill Shield
        drawPath(
            path = shieldPath,
            color = shieldColor,
            style = Fill
        )

        // Shield Border
        drawPath(
            path = shieldPath,
            color = borderColor,
            style = Stroke(width = w * 0.045f)
        )

        // 2. Draw Hardhat inside Shield
        // Hardhat Dome
        val domePath = Path().apply {
            moveTo(w * 0.30f, h * 0.54f)
            cubicTo(
                w * 0.30f, h * 0.30f,
                w * 0.70f, h * 0.30f,
                w * 0.70f, h * 0.54f
            )
            close()
        }
        drawPath(
            path = domePath,
            color = hardhatColor,
            style = Fill
        )

        // Hardhat Brim / Peak
        val brimPath = Path().apply {
            moveTo(w * 0.24f, h * 0.54f)
            lineTo(w * 0.76f, h * 0.54f)
            cubicTo(
                w * 0.74f, h * 0.60f,
                w * 0.26f, h * 0.60f,
                w * 0.24f, h * 0.54f
            )
            close()
        }
        drawPath(
            path = brimPath,
            color = hardhatColor,
            style = Fill
        )

        // Hardhat Top Ridge (Highlight)
        drawArc(
            color = Color.White.copy(alpha = 0.6f),
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(w * 0.36f, h * 0.33f),
            size = Size(w * 0.28f, h * 0.22f),
            style = Stroke(width = w * 0.035f)
        )
    }
}

/**
 * Full SafeOps Brand Header with Shield Emblem, Title and Subtitle.
 */
@Composable
fun SafeOpsBrandHeader(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 72.dp,
    textColor: Color = Color.White,
    subtitleColor: Color = Color.White.copy(alpha = 0.85f),
    showSubtitle: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SafeOpsShieldEmblem(size = emblemSize)
        
        Spacer(modifier = Modifier.height(14.dp))
        
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = textColor, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)) {
                    append("SAFE")
                }
                withStyle(SpanStyle(color = AccentOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)) {
                    append("OPS")
                }
            },
            style = SafeOpsTheme.typography.headline.copy(fontSize = 26.sp)
        )

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Construction Safety Management",
                style = SafeOpsTheme.typography.secondaryMedium.copy(
                    color = subtitleColor,
                    fontSize = 13.sp
                )
            )
        }
    }
}
