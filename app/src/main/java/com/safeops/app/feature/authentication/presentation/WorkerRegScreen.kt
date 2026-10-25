package com.safeops.app.feature.authentication.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safeops.app.core.designsystem.component.SafeOpsAuthBackground
import com.safeops.app.core.designsystem.component.SafeOpsButton
import com.safeops.app.core.designsystem.component.SafeOpsCurvyHeroPattern
import com.safeops.app.core.designsystem.component.SafeOpsDialog
import com.safeops.app.core.designsystem.component.SafeOpsOutlinedButton
import com.safeops.app.core.designsystem.component.SafeOpsPasswordField
import com.safeops.app.core.designsystem.component.SafeOpsTextField
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.core.designsystem.theme.AccentOrange
import com.safeops.app.core.designsystem.theme.DangerRed
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.designsystem.theme.SuccessGreen
import com.safeops.app.core.designsystem.theme.WarningAmber
import com.safeops.app.domain.model.WorkerRole

// ============================================================
// Worker Registration Screen
// Step 1: Enter & validate referral code
// Step 2: Worker personal details
// Step 3: Success confirmation
// Funtouch OS 16 Glass UI, Light/Dark Adaptive
// ============================================================

@Composable
fun WorkerRegScreen(
    viewModel: WorkerRegViewModel,
    onNavigateBack: () -> Unit,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = SafeOpsTheme.colors
    val scrollState = rememberScrollState()

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
                // Top bar
                WorkerRegTopBar(onNavigateBack = onNavigateBack)

                // Steps indicator (2 steps)
                WorkerStepIndicator(
                    currentStep = state.step,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

            // Animated content
            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    if (forward) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                label = "worker_reg_step"
            ) { step ->
                when (step) {
                    WorkerRegStep.REFERRAL_CODE -> ReferralCodeStep(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                    WorkerRegStep.WORKER_DETAILS -> WorkerDetailsStep(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                    WorkerRegStep.COMPLETE -> WorkerCompleteStep(
                        state = state,
                        onGoToLogin = onRegistrationComplete
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            SafeOpsDialog(
                title = "Registration Failed",
                message = state.errorMessage ?: "An error occurred. Please try again.",
                confirmButtonText = "OK",
                onConfirmClick = { viewModel.onEvent(WorkerRegEvent.DismissError) },
                onDismissRequest = { viewModel.onEvent(WorkerRegEvent.DismissError) }
            )
        }
    }
}

@Composable
private fun WorkerRegTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(SafeOpsIcons.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Worker Registration",
            style = SafeOpsTheme.typography.screenTitle.copy(
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun WorkerStepIndicator(currentStep: WorkerRegStep, modifier: Modifier = Modifier) {
    val steps = listOf(
        WorkerRegStep.REFERRAL_CODE to "Join Org",
        WorkerRegStep.WORKER_DETAILS to "Your Details",
        WorkerRegStep.COMPLETE to "Complete"
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, (step, label) ->
            val isCompleted = step.ordinal < currentStep.ordinal
            val isCurrent = step == currentStep

            // Badge Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(84.dp)
            ) {
                val circleColor = when {
                    isCompleted -> SuccessGreen
                    isCurrent -> Color.White
                    else -> Color(0xFF0F2B57) // 100% solid opaque
                }
                val numberColor = when {
                    isCompleted -> Color.White
                    isCurrent -> RoyalBluePrimary
                    else -> Color.White.copy(alpha = 0.90f)
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                        .border(
                            width = 1.5.dp,
                            color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.40f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(SafeOpsIcons.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            style = SafeOpsTheme.typography.bodyMedium.copy(
                                color = numberColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    label,
                    style = SafeOpsTheme.typography.caption.copy(
                        color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.85f),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            // Connector Line between circle edges
            if (index < steps.size - 1) {
                val isLinePassed = currentStep.ordinal > index
                val lineColor by animateColorAsState(
                    targetValue = if (isLinePassed) SuccessGreen else Color.White.copy(alpha = 0.40f),
                    animationSpec = tween(300),
                    label = "worker_line_color"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 15.dp)
                        .height(3.dp)
                        .background(lineColor, RoundedCornerShape(1.5.dp))
                )
            }
        }
    }
}

// ── Step 1: Referral Code ─────────────────────────────────────────────────
@Composable
private fun ReferralCodeStep(state: WorkerRegUiState, onEvent: (WorkerRegEvent) -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography

    WorkerStepCard {
        // Join icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(RoyalBluePrimary.copy(alpha = 0.12f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(SafeOpsIcons.QrCode, null, tint = RoyalBluePrimary, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Join Your Organization",
            style = typography.screenTitle.copy(color = colors.primaryText, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Enter the referral code shared by your\norganization admin to get started.",
            style = typography.body.copy(color = colors.secondaryText, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Referral code input
        Text(
            text = "Organization Referral Code *",
            style = typography.bodyMedium.copy(color = colors.primaryText),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Big styled code input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SafeOpsTextField(
                value = state.referralCode,
                onValueChange = { onEvent(WorkerRegEvent.ReferralCodeChanged(it)) },
                placeholder = "E.g. SAIRAMCON",
                leadingIcon = SafeOpsIcons.Key,
                isError = state.referralCodeError != null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onEvent(WorkerRegEvent.ValidateCode) }),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RoyalBluePrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(alpha = 0.3f))
                    ) { onEvent(WorkerRegEvent.ValidateCode) },
                contentAlignment = Alignment.Center
            ) {
                if (state.isValidatingCode) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(SafeOpsIcons.Search, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        AnimatedVisibility(visible = state.referralCodeError != null) {
            Text(
                text = state.referralCodeError ?: "",
                style = typography.caption.copy(color = DangerRed),
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )
        }

        // Validation result banner
        AnimatedVisibility(visible = state.codeValidated) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SuccessGreen.copy(alpha = 0.10f))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(SafeOpsIcons.Check, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Code verified!",
                            style = typography.bodyMedium.copy(color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            state.validatedOrgName,
                            style = typography.secondary.copy(color = colors.secondaryText)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = if (state.codeValidated) "Continue" else "Verify Code",
            onClick = {
                if (state.codeValidated) onEvent(WorkerRegEvent.NextStep)
                else onEvent(WorkerRegEvent.ValidateCode)
            },
            isLoading = state.isValidatingCode,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (state.codeValidated) SafeOpsIcons.ArrowForward else SafeOpsIcons.Search
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (colors.isDark) Color(0xFF0D1E35) else Color(0xFFF0F5FF))
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(SafeOpsIcons.Info, null, tint = RoyalBluePrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Your referral code is provided by your organization's safety admin. Contact them if you don't have one.",
                style = typography.caption.copy(color = colors.secondaryText)
            )
        }
    }
}

// ── Step 2: Worker Details ────────────────────────────────────────────────
@Composable
private fun WorkerDetailsStep(state: WorkerRegUiState, onEvent: (WorkerRegEvent) -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    val focus = LocalFocusManager.current

    WorkerStepCard {
        Text(
            text = "Your Details",
            style = typography.screenTitle.copy(color = colors.primaryText, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Fill in your information to complete registration.",
            style = typography.body.copy(color = colors.secondaryText)
        )

        // Org Badge
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(RoyalBluePrimary.copy(alpha = 0.10f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(SafeOpsIcons.Building, null, tint = RoyalBluePrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Joining: ${state.validatedOrgName} (${state.validatedOrgId})",
                style = typography.bodyMedium.copy(color = RoyalBluePrimary, fontWeight = FontWeight.SemiBold)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Full Name
        SafeOpsTextField(
            value = state.fullName,
            onValueChange = { onEvent(WorkerRegEvent.FullNameChanged(it)) },
            label = "Full Name *",
            placeholder = "Your full name",
            leadingIcon = SafeOpsIcons.Person,
            isError = state.fullNameError != null,
            errorMessage = state.fullNameError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Phone
        SafeOpsTextField(
            value = state.phoneNumber,
            onValueChange = { onEvent(WorkerRegEvent.PhoneNumberChanged(it)) },
            label = "Phone Number *",
            placeholder = "+ 91 98765 43210",
            leadingIcon = SafeOpsIcons.Phone,
            isError = state.phoneNumberError != null,
            errorMessage = state.phoneNumberError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Worker Role Selector (Inline expanding container inside card)
        Text(
            text = "Your Role / Designation *",
            style = typography.bodyMedium.copy(color = colors.primaryText),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        val chevronRotation by animateFloatAsState(
            targetValue = if (state.showRoleDropdown) 180f else 0f,
            animationSpec = tween(220),
            label = "role_chevron"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.secondarySurface)
                .border(
                    1.dp,
                    if (state.showRoleDropdown) colors.primary else colors.border,
                    RoundedCornerShape(14.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onEvent(WorkerRegEvent.ToggleRoleDropdown) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(SafeOpsIcons.Worker, null, tint = colors.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = state.workerRole.displayName,
                    style = typography.body.copy(color = colors.primaryText),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = SafeOpsIcons.DropdownArrow,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation)
                )
            }

            AnimatedVisibility(
                visible = state.showRoleDropdown,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    HorizontalDivider(color = colors.border.copy(alpha = 0.6f))
                    WorkerRole.entries.forEach { role ->
                        val isSelected = role == state.workerRole
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) RoyalBluePrimary.copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) {
                                    onEvent(WorkerRegEvent.WorkerRoleSelected(role))
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = role.displayName,
                                style = typography.body.copy(
                                    color = if (isSelected) colors.primary else colors.primaryText,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = SafeOpsIcons.Check,
                                    contentDescription = "Selected",
                                    tint = colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Work Location
        SafeOpsTextField(
            value = state.workLocation,
            onValueChange = { onEvent(WorkerRegEvent.WorkLocationChanged(it)) },
            label = "Work Location *",
            placeholder = "E.g. Block A in SSR Site, Chennai",
            leadingIcon = SafeOpsIcons.Location,
            isError = state.workLocationError != null,
            errorMessage = state.workLocationError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Optional: Email + Password for app login
        Text(
            text = "Optional: Create Login Credentials",
            style = typography.bodyMedium.copy(color = colors.primaryText, fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "You can also use your referral code to sign in. Set up email/password for an alternative.",
            style = typography.caption.copy(color = colors.secondaryText)
        )
        Spacer(modifier = Modifier.height(10.dp))

        SafeOpsTextField(
            value = state.email,
            onValueChange = { onEvent(WorkerRegEvent.EmailChanged(it)) },
            label = "Email Address (Optional)",
            placeholder = "worker@example.com",
            leadingIcon = SafeOpsIcons.Email,
            isError = state.emailError != null,
            errorMessage = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )

        if (state.email.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            SafeOpsPasswordField(
                value = state.password,
                onValueChange = { onEvent(WorkerRegEvent.PasswordChanged(it)) },
                label = "Password",
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
            )
            Spacer(modifier = Modifier.height(12.dp))
            SafeOpsPasswordField(
                value = state.confirmPassword,
                onValueChange = { onEvent(WorkerRegEvent.ConfirmPasswordChanged(it)) },
                label = "Confirm Password",
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Complete Registration",
            onClick = { onEvent(WorkerRegEvent.NextStep) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.Check
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ── Step 3: Worker Complete ───────────────────────────────────────────────
@Composable
private fun WorkerCompleteStep(state: WorkerRegUiState, onGoToLogin: () -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography

    WorkerStepCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(SafeOpsIcons.Check, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
        }

        Text(
            text = "Registration\nSuccessful!",
            style = typography.screenTitle.copy(
                color = colors.primaryText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You have successfully joined ${state.validatedOrgName}.\nYour safety profile is now active.",
            style = typography.body.copy(color = colors.secondaryText, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Summary card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            WorkerSummaryRow(SafeOpsIcons.Person, "Name", state.fullName)
            Spacer(modifier = Modifier.height(8.dp))
            WorkerSummaryRow(SafeOpsIcons.Worker, "Role", state.workerRole.displayName)
            Spacer(modifier = Modifier.height(8.dp))
            WorkerSummaryRow(SafeOpsIcons.Location, "Work Location", state.workLocation)
            Spacer(modifier = Modifier.height(8.dp))
            WorkerSummaryRow(SafeOpsIcons.Building, "Organization", state.validatedOrgName)
        }

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Go to Sign In",
            onClick = onGoToLogin,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.ArrowForward
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun WorkerSummaryRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = RoyalBluePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = typography.caption.copy(color = colors.secondaryText))
            Text(value, style = typography.bodyMedium.copy(color = colors.primaryText, fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun WorkerStepCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = SafeOpsTheme.colors
    val isDark = colors.isDark

    val cardColor = if (isDark) Color(0xFF0C182A).copy(alpha = 0.88f)
    else Color.White.copy(alpha = 0.98f)

    val borderBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF82B1FF).copy(alpha = 0.50f),
                Color(0xFF1E3A60).copy(alpha = 0.20f)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(cardColor)
            .border(1.dp, borderBrush, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        content()
    }
}
