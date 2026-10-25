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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
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
import com.safeops.app.core.designsystem.theme.BrightBlueAccent
import com.safeops.app.core.designsystem.theme.RoyalBluePrimary
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.designsystem.theme.SuccessGreen
import com.safeops.app.domain.model.IndustryType

// ============================================================
// Admin Registration Screen — 4-Step Wizard
// Funtouch OS 16 Glass UI, Light/Dark Adaptive
// ============================================================

@Composable
fun AdminRegScreen(
    viewModel: AdminRegViewModel,
    onNavigateBack: () -> Unit,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
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
            // ── Top Bar with back button + logo ──────────────────────
            AdminRegTopBar(
                onNavigateBack = onNavigateBack,
                isDark = colors.isDark
            )

            // ── Step Progress Indicator ───────────────────────────────
            StepProgressIndicator(
                currentStep = state.step,
                onStepClick = { step ->
                    viewModel.onEvent(AdminRegEvent.NavigateToStep(step))
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // ── Animated Step Content ────────────────────────────────
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
                label = "step_content"
            ) { step ->
                when (step) {
                    AdminRegStep.ADMIN_DETAILS -> AdminDetailsStep(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                    AdminRegStep.ORGANIZATION -> OrganizationStep(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                    AdminRegStep.REVIEW -> ReviewStep(
                        state = state,
                        onEvent = viewModel::onEvent
                    )
                    AdminRegStep.COMPLETE -> CompleteStep(
                        state = state,
                        onGoToDashboard = onRegistrationComplete
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            SafeOpsDialog(
                title = "Registration Failed",
                message = state.errorMessage ?: "An error occurred. Please try again.",
                confirmButtonText = "OK",
                onConfirmClick = { viewModel.onEvent(AdminRegEvent.DismissError) },
                onDismissRequest = { viewModel.onEvent(AdminRegEvent.DismissError) }
            )
        }
    }
}

// ── Top Bar ─────────────────────────────────────────────────────────────
@Composable
private fun AdminRegTopBar(onNavigateBack: () -> Unit, isDark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = SafeOpsIcons.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Row {
                Text(
                    text = "Safe ",
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Ops",
                    style = SafeOpsTheme.typography.cardTitle.copy(
                        color = AccentOrange,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                text = "Safety \u2022 Compliance \u2022 Operations",
                style = SafeOpsTheme.typography.caption.copy(
                    color = Color.White.copy(alpha = 0.75f)
                )
            )
        }
    }
}

// ── Step Progress Indicator ──────────────────────────────────────────────
@Composable
private fun StepProgressIndicator(
    currentStep: AdminRegStep,
    onStepClick: (AdminRegStep) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = AdminRegStep.entries
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = step.ordinal < currentStep.ordinal
            val isCurrent = step == currentStep

            // Badge Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { if (isCompleted) onStepClick(step) }
            ) {
                val circleColor by animateColorAsState(
                    targetValue = when {
                        isCompleted -> SuccessGreen
                        isCurrent -> Color.White
                        else -> Color(0xFF0F2B57) // 100% solid opaque
                    },
                    animationSpec = tween(300),
                    label = "circle_color"
                )
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
                            width = if (isCurrent) 2.dp else 1.5.dp,
                            color = if (isCurrent) RoyalBluePrimary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.35f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = SafeOpsIcons.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = step.number.toString(),
                            style = SafeOpsTheme.typography.bodyMedium.copy(
                                color = numberColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = step.label,
                    style = SafeOpsTheme.typography.caption.copy(
                        color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.80f),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Connector Line between circle edges
            if (index < steps.size - 1) {
                val isLinePassed = currentStep.ordinal > index
                val lineColor by animateColorAsState(
                    targetValue = if (isLinePassed) SuccessGreen else Color.White.copy(alpha = 0.35f),
                    animationSpec = tween(300),
                    label = "line_color"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .offset(y = (-10).dp)
                        .height(3.dp)
                        .background(lineColor, RoundedCornerShape(1.5.dp))
                )
            }
        }
    }
}

// ── STEP 1: Admin Details ─────────────────────────────────────────────────
@Composable
private fun AdminDetailsStep(state: AdminRegUiState, onEvent: (AdminRegEvent) -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    val focus = LocalFocusManager.current

    RegStepCard {
        Text(
            text = "Create Admin Account",
            style = typography.screenTitle.copy(color = colors.primaryText, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Set up your account to manage your organization.",
            style = typography.body.copy(color = colors.secondaryText)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Full Name
        SafeOpsTextField(
            value = state.fullName,
            onValueChange = { onEvent(AdminRegEvent.FullNameChanged(it)) },
            label = "Full Name",
            placeholder = "e.g. John Anderson",
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

        // Email
        SafeOpsTextField(
            value = state.email,
            onValueChange = { onEvent(AdminRegEvent.EmailChanged(it)) },
            label = "Email Address",
            placeholder = "e.g. john.anderson@apexinfra.com",
            leadingIcon = SafeOpsIcons.Email,
            isError = state.emailError != null,
            errorMessage = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Password
        SafeOpsPasswordField(
            value = state.password,
            onValueChange = { onEvent(AdminRegEvent.PasswordChanged(it)) },
            label = "Password",
            isError = state.passwordError != null,
            errorMessage = state.passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )

        // Password strength checklist
        Spacer(modifier = Modifier.height(8.dp))
        PasswordStrengthRow(label = "At least 8 characters", met = state.hasMinLength)
        PasswordStrengthRow(label = "One uppercase letter", met = state.hasUppercase)
        PasswordStrengthRow(label = "One number", met = state.hasNumber)
        PasswordStrengthRow(label = "One special character", met = state.hasSpecialChar)

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password
        SafeOpsPasswordField(
            value = state.confirmPassword,
            onValueChange = { onEvent(AdminRegEvent.ConfirmPasswordChanged(it)) },
            label = "Confirm Password",
            isError = state.confirmPasswordError != null,
            errorMessage = state.confirmPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Phone (Optional)
        SafeOpsTextField(
            value = state.phoneNumber,
            onValueChange = { onEvent(AdminRegEvent.PhoneNumberChanged(it)) },
            label = "Phone Number (Optional)",
            placeholder = "+ 91 98765 43210",
            leadingIcon = SafeOpsIcons.Phone,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
        )

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Next",
            onClick = { onEvent(AdminRegEvent.NextStep) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.ArrowForward
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sign in link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                style = typography.body.copy(color = colors.secondaryText)
            )
            Text(
                text = "Sign In",
                style = typography.body.copy(
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { /* navigate back */ }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PasswordStrengthRow(label: String, met: Boolean) {
    val colors = SafeOpsTheme.colors
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (met) SafeOpsIcons.Check else SafeOpsIcons.Circle,
            contentDescription = null,
            tint = if (met) SuccessGreen else colors.disabled,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = SafeOpsTheme.typography.caption.copy(
                color = if (met) SuccessGreen else colors.secondaryText
            )
        )
    }
}

// ── STEP 2: Organization Details ──────────────────────────────────────────
@Composable
private fun OrganizationStep(state: AdminRegUiState, onEvent: (AdminRegEvent) -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    val focus = LocalFocusManager.current

    RegStepCard {
        Text(
            text = "Organization Details",
            style = typography.screenTitle.copy(color = colors.primaryText, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tell us about your organization.",
            style = typography.body.copy(color = colors.secondaryText)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Org Name
        SafeOpsTextField(
            value = state.orgName,
            onValueChange = { onEvent(AdminRegEvent.OrgNameChanged(it)) },
            label = "Organization Name *",
            placeholder = "e.g. Apex Infrastructure Ltd",
            leadingIcon = SafeOpsIcons.Building,
            isError = state.orgNameError != null,
            errorMessage = state.orgNameError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Org ID (auto-generated short code)
        SafeOpsTextField(
            value = state.orgId,
            onValueChange = { onEvent(AdminRegEvent.OrgIdChanged(it)) },
            label = "Organization ID (Short Code) *",
            placeholder = "e.g. APEXINFRA",
            leadingIcon = SafeOpsIcons.Badge,
            isError = state.orgIdError != null,
            errorMessage = state.orgIdError,
            trailingIconVector = when (state.orgIdAvailable) {
                true -> SafeOpsIcons.Check
                false -> SafeOpsIcons.Error
                null -> SafeOpsIcons.Info
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Text(
            text = "This unique code will be used for worker registration.",
            style = typography.caption.copy(color = colors.secondaryText),
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Industry Type Selector (Inline expanding container inside card)
        Text(
            text = "Industry Type *",
            style = typography.bodyMedium.copy(color = colors.primaryText),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        val industryChevronRotation by animateFloatAsState(
            targetValue = if (state.showIndustryDropdown) 180f else 0f,
            animationSpec = tween(220),
            label = "industry_chevron"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.secondarySurface)
                .border(
                    1.dp,
                    if (state.showIndustryDropdown) colors.primary else colors.border,
                    RoundedCornerShape(14.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onEvent(AdminRegEvent.ToggleIndustryDropdown) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = SafeOpsIcons.Building,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = state.industryType.displayName,
                    style = typography.body.copy(color = colors.primaryText),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = SafeOpsIcons.DropdownArrow,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(industryChevronRotation)
                )
            }

            AnimatedVisibility(
                visible = state.showIndustryDropdown,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    HorizontalDivider(color = colors.border.copy(alpha = 0.6f))
                    IndustryType.entries.forEach { type ->
                        val isSelected = type == state.industryType
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
                                    onEvent(AdminRegEvent.IndustryTypeSelected(type))
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.displayName,
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

        // Address
        SafeOpsTextField(
            value = state.address,
            onValueChange = { onEvent(AdminRegEvent.AddressChanged(it)) },
            label = "Address *",
            placeholder = "123, Industrial Area, Sriperumbudur, Chennai - 602105",
            leadingIcon = SafeOpsIcons.Location,
            isError = state.addressError != null,
            errorMessage = state.addressError,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Contact Number
        SafeOpsTextField(
            value = state.contactNumber,
            onValueChange = { onEvent(AdminRegEvent.ContactNumberChanged(it)) },
            label = "Organization Contact Number *",
            placeholder = "+ 91 44 1234 5678",
            leadingIcon = SafeOpsIcons.Phone,
            isError = state.contactNumberError != null,
            errorMessage = state.contactNumberError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) })
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Website (optional)
        SafeOpsTextField(
            value = state.website,
            onValueChange = { onEvent(AdminRegEvent.WebsiteChanged(it)) },
            label = "Organization Website (Optional)",
            placeholder = "https://www.example.com",
            leadingIcon = SafeOpsIcons.Web,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
        )

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Next",
            onClick = { onEvent(AdminRegEvent.NextStep) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.ArrowForward
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ── STEP 3: Review & Confirm ──────────────────────────────────────────────
@Composable
private fun ReviewStep(state: AdminRegUiState, onEvent: (AdminRegEvent) -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography

    RegStepCard {
        Text(
            text = "Review & Confirm",
            style = typography.screenTitle.copy(color = colors.primaryText, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Please review your details before creating the admin account and organization.",
            style = typography.body.copy(color = colors.secondaryText)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Admin Details Card
        ReviewSectionCard(
            icon = SafeOpsIcons.Person,
            title = "Admin Details",
            onEdit = { onEvent(AdminRegEvent.NavigateToStep(AdminRegStep.ADMIN_DETAILS)) }
        ) {
            ReviewRow("Full Name", state.fullName)
            ReviewRow("Email Address", state.email)
            ReviewRow("Phone Number", state.phoneNumber.ifBlank { "—" })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Org Details Card
        ReviewSectionCard(
            icon = SafeOpsIcons.Building,
            title = "Organization Details",
            onEdit = { onEvent(AdminRegEvent.NavigateToStep(AdminRegStep.ORGANIZATION)) }
        ) {
            ReviewRow("Organization Name", state.orgName)
            ReviewRow("Organization ID", state.orgId)
            ReviewRow("Industry Type", state.industryType.displayName)
            ReviewRow("Address", state.address)
            ReviewRow("Contact Number", state.contactNumber)
            if (state.website.isNotBlank()) ReviewRow("Website", state.website)
        }

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Create Account",
            onClick = { onEvent(AdminRegEvent.Submit) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.ArrowForward
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ReviewSectionCard(
    icon: ImageVector,
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RoyalBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = RoyalBluePrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = typography.cardTitle.copy(color = colors.primaryText, fontWeight = FontWeight.SemiBold)
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onEdit() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(SafeOpsIcons.Edit, null, tint = RoyalBluePrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Edit",
                    style = typography.caption.copy(color = RoyalBluePrimary, fontWeight = FontWeight.SemiBold)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = colors.border)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = typography.secondary.copy(color = colors.secondaryText),
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = typography.secondary.copy(color = colors.primaryText, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(0.55f),
            textAlign = TextAlign.End
        )
    }
}

// ── STEP 4: Complete ──────────────────────────────────────────────────────
@Composable
private fun CompleteStep(state: AdminRegUiState, onGoToDashboard: () -> Unit) {
    val colors = SafeOpsTheme.colors
    val typography = SafeOpsTheme.typography
    val clipboardManager = LocalClipboardManager.current
    val result = state.registrationResult

    RegStepCard {
        // Success animation placeholder (use Lottie in production)
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
                    Icon(
                        imageVector = SafeOpsIcons.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Text(
            text = "Account Created\nSuccessfully!",
            style = typography.screenTitle.copy(
                color = colors.primaryText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your admin account and organization have\nbeen created successfully.",
            style = typography.body.copy(color = colors.secondaryText, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Org Summary Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RoyalBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(SafeOpsIcons.Building, null, tint = RoyalBluePrimary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = result?.organizationName ?: "Your Organization",
                        style = typography.cardTitle.copy(color = colors.primaryText, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = result?.orgId ?: "",
                        style = typography.secondary.copy(color = colors.secondaryText)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Org ID ready indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SuccessGreen.copy(alpha = 0.10f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(SafeOpsIcons.Check, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Organization ID is ready!",
                        style = typography.bodyMedium.copy(color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Share this code with your workers so they can join your organization.",
                        style = typography.caption.copy(color = colors.secondaryText)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Copy-able referral code
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.secondarySurface, colors.secondarySurface)
                        )
                    )
                    .border(1.dp, colors.border.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) {
                        result?.referralCode?.let {
                            clipboardManager.setText(AnnotatedString(it))
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = result?.referralCode ?: "",
                    style = typography.sectionTitle.copy(
                        color = colors.primaryText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                )
                Icon(SafeOpsIcons.Copy, null, tint = colors.primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(RoyalBluePrimary.copy(alpha = 0.08f))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(SafeOpsIcons.Info, null, tint = RoyalBluePrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You can manage your organization, invite workers and set up projects from the dashboard.",
                    style = typography.caption.copy(color = colors.secondaryText)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        SafeOpsButton(
            text = "Go to Dashboard",
            onClick = onGoToDashboard,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = SafeOpsIcons.ArrowForward
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ── Shared step card wrapper ──────────────────────────────────────────────
@Composable
private fun RegStepCard(content: @Composable () -> Unit) {
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

@Composable
private fun OrSeparator() {
    val colors = SafeOpsTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.border)
        Text(
            "  OR  ",
            style = SafeOpsTheme.typography.caption.copy(color = colors.secondaryText)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.border)
    }
}
