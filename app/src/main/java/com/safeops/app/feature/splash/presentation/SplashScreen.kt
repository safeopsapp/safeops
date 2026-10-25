package com.safeops.app.feature.splash.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safeops.app.R
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession
import com.safeops.app.domain.repository.SessionRepository
import com.safeops.app.feature.dashboards.presentation.SafeOpsHeroPattern
import kotlinx.coroutines.delay

/**
 * Unified Airtel-Style Single Splash Screen.
 * Features:
 * - Single entry point without secondary splash screen flicker
 * - Rich corporate blue radial gradient
 * - Signature Airtel-inspired fluid curved swoops & wave devices at the bottom
 * - Pulsing brand emblem and typography
 * - Smooth transition to Authentication or Dashboard
 */
@Composable
fun SplashScreen(
    sessionRepository: SessionRepository,
    onNavigateToDashboard: (AuthenticatedSession) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1200) // Optimal launch duration for brand recognition
        try {
            val session = sessionRepository.getCurrentSession()
            if (session != null) {
                onNavigateToDashboard(session)
            } else {
                onNavigateToLogin()
            }
        } catch (_: Exception) {
            onNavigateToLogin()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val progressAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "progress_bar"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val radius = if (screenHeight > 0f) screenHeight * 0.90f else 1800f

        val splashGradient = Brush.radialGradient(
            0.00f to Color(0xFF0878FF),
            0.28f to Color(0xFF063D9D),
            0.58f to Color(0xFF031B45),
            1.00f to Color(0xFF010B1E),
            center = Offset(screenWidth / 2f, 0f),
            radius = radius
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(splashGradient)
        ) {
            // Blueprint pattern device
            SafeOpsHeroPattern(isDark = true, modifier = Modifier.matchParentSize())

            // Luminous ambient top flare
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.35f),
                                Color(0xFF0878FF).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(screenWidth / 2f, 0f),
                            radius = 650f
                        )
                    )
            )

            // Airtel-Style Sweeping Curved Wave Device at Bottom
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.BottomCenter)
            ) {
                val w = size.width
                val h = size.height

                // Wave Layer 1: Deep Translucent Curve
                val path1 = Path().apply {
                    moveTo(0f, h * 0.40f)
                    cubicTo(
                        w * 0.25f, h * 0.10f,
                        w * 0.70f, h * 0.70f,
                        w, h * 0.25f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = path1,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF063D9D).copy(alpha = 0.45f),
                            Color(0xFF010B1E).copy(alpha = 0.85f)
                        )
                    )
                )

                // Wave Layer 2: Glowing Cyan Accent Wave
                val path2 = Path().apply {
                    moveTo(0f, h * 0.65f)
                    cubicTo(
                        w * 0.35f, h * 0.35f,
                        w * 0.65f, h * 0.85f,
                        w, h * 0.45f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = path2,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.25f),
                            Color(0xFF0878FF).copy(alpha = 0.10f)
                        )
                    )
                )

                // High-precision accent stroke along curve crest
                val strokePath = Path().apply {
                    moveTo(0f, h * 0.65f)
                    cubicTo(
                        w * 0.35f, h * 0.35f,
                        w * 0.65f, h * 0.85f,
                        w, h * 0.45f
                    )
                }
                drawPath(
                    path = strokePath,
                    color = Color(0xFF38BDF8).copy(alpha = 0.40f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }

            // Central Brand Identity & App Logo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Pulsing Logo Emblem Container
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(116.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0878FF), Color(0xFF063D9D))
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.verticalGradient(
                                listOf(Color(0xFF38BDF8).copy(alpha = 0.8f), Color.Transparent)
                            ),
                            RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_safeops_logo),
                        contentDescription = "SafeOps App Icon",
                        modifier = Modifier.size(76.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Typography ("Safe" White + "Ops" Orange)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SAFE",
                        style = SafeOpsTheme.typography.display.copy(
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "OPS",
                        style = SafeOpsTheme.typography.display.copy(
                            color = AccentOrange,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Construction Safety & PTW Compliance",
                    style = SafeOpsTheme.typography.secondaryMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Modern Sleek Progress Bar Indicator
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(3.5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressAnim)
                            .height(3.5.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF0878FF))
                                )
                            )
                    )
                }
            }

            // Enterprise Footer Tag
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SPRINGFIELD INFRASTRUCTURE OPERATIONS",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
