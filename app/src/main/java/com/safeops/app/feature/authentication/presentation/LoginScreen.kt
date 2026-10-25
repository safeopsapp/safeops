package com.safeops.app.feature.authentication.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role as SemanticsRole
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.safeops.app.R
import com.safeops.app.core.designsystem.component.SafeOpsAuthBackground
import com.safeops.app.core.designsystem.component.SafeOpsButton
import com.safeops.app.core.designsystem.component.SafeOpsCurvyHeroPattern
import com.safeops.app.core.designsystem.component.SafeOpsDialog
import com.safeops.app.core.designsystem.component.SafeOpsPasswordField
import com.safeops.app.core.designsystem.component.SafeOpsTextField
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.BrightBlueAccent
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.domain.model.AuthenticatedSession

enum class LoginRole { STAFF, ADMIN }

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (AuthenticatedSession) -> Unit,
    onNavigateToAdminRegister: () -> Unit,
    onNavigateToWorkerRegister: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val webClientId = "597325401037-fn5ib7icouh81g9cq2lvli9hjo4uqrma.apps.googleusercontent.com"

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.onEvent(LoginEvent.GoogleSignIn(idToken))
            } else {
                viewModel.onEvent(LoginEvent.GoogleSignInFailed("Google Sign-In did not return an ID token."))
            }
        } catch (e: ApiException) {
            if (e.statusCode != 12501 && e.statusCode != 12502) {
                viewModel.onEvent(
                    LoginEvent.GoogleSignInFailed(
                        "Google Sign-In failed (${e.statusCode}): ${e.localizedMessage ?: "Please try again."}"
                    )
                )
            }
        }
    }

    LaunchedEffect(state.successSession) {
        state.successSession?.let { onLoginSuccess(it) }
    }

    SafeOpsAuthBackground(
        modifier = Modifier.fillMaxSize(),
        isDark = colors.isDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
                // ── Clean Typography Header (Reference Image 2) ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SAFE",
                            style = typography.display.copy(
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.5.sp
                            )
                        )
                        Text(
                            text = "OPS",
                            style = typography.display.copy(
                                color = AccentOrange,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Construction Safety & Operations Management",
                        style = typography.secondary.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                // ── Crystal Glass Floating Form Card with Glowing Borders ──
                GlassFormCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isDark = colors.isDark
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp, vertical = 26.dp)
                    ) {
                    // Role tab switcher (Worker | Admin) with crystal glass pill style
                    RoleTabSwitcher(
                        selectedRole = state.selectedLoginRole,
                        onRoleSelected = { viewModel.onEvent(LoginEvent.SelectLoginRole(it)) },
                        isDark = colors.isDark
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // User ID or Email input (Single clean label)
                    SafeOpsTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                        label = "User ID or Email",
                        placeholder = "Enter your user ID or email",
                        leadingIcon = SafeOpsIcons.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = state.emailError != null,
                        errorMessage = state.emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password input (Single clean label)
                    SafeOpsPasswordField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                        label = "Password",
                        placeholder = "Enter your password",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.onEvent(LoginEvent.Submit)
                            }
                        ),
                        isError = state.passwordError != null,
                        errorMessage = state.passwordError
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Remember me + Forgot password row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(),
                                    role = SemanticsRole.Checkbox
                                ) {
                                    viewModel.onEvent(LoginEvent.ToggleRememberMe)
                                }
                        ) {
                            Checkbox(
                                checked = state.rememberMe,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colors.primary,
                                    uncheckedColor = colors.border
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Remember me",
                                style = typography.body.copy(color = colors.primaryText, fontSize = 13.5.sp)
                            )
                        }

                        Text(
                            text = "Forgot password?",
                            style = typography.body.copy(
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) { /* Forgot password action */ }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Blue Gradient Sign In button
                    SafeOpsButton(
                        text = "Sign in",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.onEvent(LoginEvent.Submit)
                        },
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = SafeOpsIcons.ArrowForward
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // — or continue with —
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = colors.border.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "   or continue with   ",
                            style = typography.caption.copy(color = colors.secondaryText)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = colors.border.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign-In Button with official logo
                    GoogleSignInButton(
                        isDark = colors.isDark,
                        border = colors.border,
                        textColor = colors.primaryText,
                        onClick = {
                            focusManager.clearFocus()
                            googleSignInClient.signOut().addOnCompleteListener {
                                googleLauncher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contextual registration link based on active tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (promptText, linkText, onLink) = when (state.selectedLoginRole) {
                            LoginRole.ADMIN -> Triple(
                                "Don't have an account? ",
                                "Register as Admin",
                                onNavigateToAdminRegister
                            )
                            LoginRole.STAFF -> Triple(
    "New staff? ",
    "Join with Referral Code",
    onNavigateToWorkerRegister
)
                        }
                        Text(
                            text = promptText,
                            style = typography.body.copy(color = colors.secondaryText, fontSize = 13.5.sp)
                        )
                        Text(
                            text = linkText,
                            style = typography.body.copy(
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) { onLink() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Security & Copyright footer
                    Text(
                        text = "Secure access only. Do not share your credentials.\nCopyright \u00A9 2026 SafeOps. Ver 1.0.1",
                        style = typography.caption.copy(
                            color = colors.secondaryText.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Error Dialog
        if (state.errorMessage != null) {
            SafeOpsDialog(
                title = "Sign In Failed",
                message = state.errorMessage ?: "An unexpected error occurred.",
                confirmButtonText = "OK",
                onConfirmClick = { viewModel.onEvent(LoginEvent.DismissError) },
                onDismissRequest = { viewModel.onEvent(LoginEvent.DismissError) }
            )
        }
    }
}

// ── Role Tab Switcher (Worker | Admin) ───────────────────────────────────
@Composable
private fun RoleTabSwitcher(
    selectedRole: LoginRole,
    onRoleSelected: (LoginRole) -> Unit,
    isDark: Boolean
) {
    val typography = SafeOpsTheme.typography
    val containerColor = if (isDark) Color(0xFF18181B) else Color(0xFFEBF2FF).copy(alpha = 0.75f)
    val containerBorder = if (isDark) Color(0xFF333338) else Color(0xFFCFE0FC)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, containerBorder, RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            LoginRole.entries.forEach { role ->
                val isSelected = selectedRole == role
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White
                    else if (isDark) Color(0xFFA6A3AA) else Color(0xFF68758E),
                    animationSpec = tween(180),
                    label = "tab_text"
                )
                
                val tabModifier = if (isSelected) {
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0878FF), Color(0xFF005AE0))
                            )
                        )
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.60f), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                }

                Row(
                    modifier = tabModifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White.copy(alpha = 0.25f))
                        ) { onRoleSelected(role) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (role == LoginRole.STAFF) SafeOpsIcons.Worker else SafeOpsIcons.Admin,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = typography.bodyMedium.copy(
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

// ── Glass Form Card (Crystal Transparent with Glowing Neon Blue Edges) ─
@Composable
private fun GlassFormCard(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val topRadius = 32.dp
    val cardBackground = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF18181B),
                Color(0xFF000000)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.98f),
                Color(0xFFF9FBFF)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF38BDF8).copy(alpha = 0.85f), // Neon cyan-blue glowing top edge
                Color(0xFF0878FF).copy(alpha = 0.40f),
                Color(0xFF333338).copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE2EAF8),
                Color(0xFFD6E2F5)
            )
        )
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // ── Above Transparent Glass Curved Divider (Image 2 Style - Zero Gap Behind Main Card) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    if (isDark) Brush.verticalGradient(
                        listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color(0xFF0878FF).copy(alpha = 0.15f))
                    ) else Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.70f), Color.White.copy(alpha = 0.25f))
                    )
                )
                .border(
                    1.dp,
                    if (isDark) Brush.verticalGradient(
                        listOf(Color(0xFF38BDF8).copy(alpha = 0.75f), Color(0xFF0878FF).copy(alpha = 0.30f))
                    ) else Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.40f))
                    ),
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
        )

        // ── Main Card (Starts at y = 14.dp, perfectly overlapping the lower 34.dp of the glass plate) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 14.dp)
                .clip(RoundedCornerShape(topStart = topRadius, topEnd = topRadius))
                .background(cardBackground)
                .border(
                    1.5.dp,
                    borderBrush,
                    RoundedCornerShape(topStart = topRadius, topEnd = topRadius)
                )
        ) {
            content()
        }
    }
}

// ── Google Sign-In Button ──────────────────────────────────────────────
@Composable
private fun GoogleSignInButton(
    isDark: Boolean,
    border: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val typography = SafeOpsTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color(0xFF18181B) else Color.White)
            .border(1.dp, if (isDark) Color(0xFF333338) else border, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() }
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Continue with Google",
            style = typography.bodyMedium.copy(
                color = if (isDark) Color.White else textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
